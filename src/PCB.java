import java.io.*;
import java.util.Queue;



public class PCB {

    public byte priority;
    public short processID;
    public short dataSize;
    public static final int pageSize = 128;
    final String fileName;
    public byte[] data;
    public byte[] code;
    public byte[] stack;
    public int stackCounter;
    public short[] gpr;
    public int dataPages;
    public int codePages;
    public int stackPages;
    public int[] dataPagingTable;
    public int[] codePagingTable;
    public int[] stackPagingTable;
    public SpecialRegisters spr;

    public PCB (String filename) {
        this.fileName = filename;
        gpr =  new short[16];
        spr = new SpecialRegisters();
        String inputFile = filename;
        try (
            InputStream inputStream = new FileInputStream(inputFile);
        ) 
        {
            priority =   (byte) inputStream.read();
            processID =   (short) (256 * inputStream.read() + inputStream.read()) ;
            dataSize =   (short) (256 * inputStream.read() +  inputStream.read()) ;
    
        }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        this.setData();
        this.setCode();
        this.setStack();
        this.setPages();
        dataPagingTable = new int[dataPages+1];
        codePagingTable = new int[codePages+1];
        stackPagingTable = new int[stackPages+1];
    }

    //toString function for pcb, displays its contents
    public String toString(){
        return "the priority is"+  this.priority +
        " \n the Process ID  is "+  this.processID + 
        "\n the data size  is "+  this.dataSize  + 
        "\n the spr contains "+  this.spr +
        "\n the gpr contains "+  RegistersToString(gpr)
        ;
    }

    //returns the stringified valus of registers
    public String RegistersToString(short[] gpr) {
        String s = "";
        for (int i = 0; i < gpr.length; i++) {
            //System.out.print("r" + (i) + ": " + gpr[i] + " ");
            s += gpr[i] + " ";
        }
        s += "\n";
        return s;
    }

    //updates the registers within a pcb, to the given register
    public void updateRegisters(SpecialRegisters spr, short[] gpr){
        for(int i = 0; i < 16; i++){
        this.gpr[i] = gpr[i];
        this.spr.registers[i] = spr.registers[i];
        this.spr.flagRegister[i] = spr.flagRegister[i];
        }
    }

    //retrieves the registers within the pcb, and updates 
    //the given registers to their values
    public void cloneRegisters(SpecialRegisters spr, short[] gpr){
        spr = new SpecialRegisters();
        gpr = new short[16];
        for(int i = 0; i < 16; i++){
            gpr[i] = this.gpr[i];
            spr.registers[i] = this.spr.registers[i];
            spr.flagRegister[i] = this.spr.flagRegister[i];
        }
    }


    public void setPages(){
        this.dataPages = (this.dataSize- 1) / this.pageSize;
        this.codePages = (this.code.length - 1) / this.pageSize;
        this.stackPages = (this.stack.length - 1) / this.pageSize;
    }

    //loads the data from the file into the data array
    private void setData(){
        try (
        InputStream inputStream = new FileInputStream(this.fileName);
        ) 
        {
            inputStream.skipNBytes(8);
            this.data = inputStream.readNBytes(this.dataSize);
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void setCode(){
        try (
            InputStream inputStream = new FileInputStream(this.fileName);
        ) 
        {   
            inputStream.skipNBytes(8 + this.dataSize);
            this.code = inputStream.readAllBytes();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void setStack(){
        this.stack = new byte[200];
        stackCounter = 0;
    }
    //loads the process data, code and stack into memory
    public void loadProcessIntoMem(byte[] memory, Queue<Integer> frameQueue){
        try{
        loadArrIntoMem(memory, frameQueue, "data");
        loadArrIntoMem(memory, frameQueue, "code");
        loadArrIntoMem(memory, frameQueue, "stack");
        }
        catch (Exception e){
        System.out.println(e);
        }
    }

    //helper function for laodProcessIntoMem
    private void loadArrIntoMem(byte[] memory, Queue<Integer> frameQueue, String type) throws Exception{
        int[] genericPagingTable = null;
        byte[] generic = null;
        genericPagingTable = getTableOnType(type);
        generic = getArrOnType(type);
        byte[] arr = new byte[this.pageSize];
        for(int i = 0; i < genericPagingTable.length; i++){
        arr = new byte[this.pageSize];
        for(int j = 0; j < this.pageSize; j++){
            if((i* this.pageSize + j) == generic.length) break;
            arr[j] = generic[i* this.pageSize + j];
        }
        genericPagingTable[i] = Paging.loadDataIntoFrame(frameQueue, memory, arr, this.pageSize);
        }
    }

    //removes a process code, data and stack from memory
    public void removeProcesFromMem(byte[] memory, Queue<Integer> frameQueue){
        try {
        removeTypeFromMem(memory, frameQueue, "data");
        removeTypeFromMem(memory, frameQueue, "code");
        removeTypeFromMem(memory, frameQueue, "stack");
        } catch (Exception e) {
        e.printStackTrace();
        }
    }
    
    //helper function for removeProcessFromMem
    private void removeTypeFromMem(byte[] memory, Queue<Integer> frameQueue, String type) throws Exception{
        int[] genericPagingTable = null;
        genericPagingTable = getTableOnType(type);
        for(int i = 0; i < genericPagingTable.length; i++){
        Paging.emptyDataFromFrame(memory, frameQueue, genericPagingTable[i], this.pageSize);
        genericPagingTable[i] = 0;
        }
    }

    //gets the paging table for code/data/stack
    private int[] getTableOnType(String type) throws Exception{
        int[] genericPagingTable = null;
        switch(type){
        case "data": 
        {
            genericPagingTable = this.dataPagingTable;
            break;
        }
        case "code": 
        {
            genericPagingTable = this.codePagingTable;
            break;
        }
        case "stack": 
        {
            genericPagingTable = this.stackPagingTable;
            break;
        }
        default: throw new Exception("Not data, code, or stack");
        }
        return genericPagingTable;
    }

    //gets the code/data/stack
    private byte[] getArrOnType(String type) throws Exception{
        byte[] generic = null;
        switch(type){
        case "data": 
        {
            generic = this.data;
            break;
        }
        case "code": 
        {
            generic = this.code;
            break;
        }
        case "stack": 
        {
            generic = this.stack;
            break;
        }
        default: throw new Exception("Not data, code, or stack");
        }
        return generic;
    }

    //returns the physical address when given the 
    //logical address for the data
    public int getPhysicalAdressForData(int logicalAddress){
        int pageNum = logicalAddress / this.pageSize;
        int frameNum = this.dataPagingTable[pageNum];
        return frameNum * this.pageSize + logicalAddress % this.pageSize;
    }

    //as above but for code
    public int getPhysicalAdressForCode(int logicalAddress){
        int pageNum = logicalAddress / this.pageSize;
        int frameNum = this.codePagingTable[pageNum];
        return frameNum * this.pageSize + logicalAddress % this.pageSize;
    }

    //as above but for stack
    public int getPhysicalAdressForStack(int logicalAddress){
        int pageNum = logicalAddress / this.pageSize;
        int frameNum = this.stackPagingTable[pageNum];
        return frameNum * this.pageSize + logicalAddress % this.pageSize;
    }
    
    public void pushOnStack(byte[] memory, byte data){
        memory[getPhysicalAdressForStack(this.stackCounter)] = data;
        stackCounter++;
    }

    public byte popFromStack(byte[] memory){
        memory[getPhysicalAdressForStack(this.stackCounter-1)] = 0;
        this.stackCounter--;
        return memory[getPhysicalAdressForStack(this.stackCounter)];
    }

    public void saveDataToMemory(byte[] memory, byte data, int logicalAddress){
        memory[getPhysicalAdressForData(logicalAddress)] = data;
    }

    public byte getDataFromMemory(byte[] memory, int logicalAddress){
        return memory[getPhysicalAdressForData(logicalAddress)];
    }

    public byte getInstructionFromMemory(byte[] memory, int programCounter){
        return memory[getPhysicalAdressForCode(programCounter)];
    }
    // to String function for printing an array[String]
    public static String printArr(byte[] arr){
        String s = "";
        for(int i = 0; i < arr.length; i++){
        s += arr[i] + "\n";
        }
        return s;
    }

    // to String function for printing an array[int]
    public static String printArr(int[] arr){
        String s = "";
        for(int i = 0; i < arr.length; i++){
        s += arr[i] + "\n";
        }
        return s;
    }

}