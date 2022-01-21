

/*
register indexes
0 = 0 Register (According to Sir's doc the value of the first register is always 0)
1 = Code Base
represents memory address from which instructios start
2 = Code Limit
    end of instructions
3 = Code Counter
    offset from start of instructions
4 = Stack Base
    start of stack 
5 = Stack Limit
    end of stack
6 = Stack Counter
    offset from start of stack
7 = Data Base
8 = Data Limit
9 = Data Counter
10 = Program Counter
11 = Instruction Register

FLAG REGISTER
0 = Carry
1 = Zero
2 = Sign
3 = Overflow
 */

public class SpecialRegisters {
    public short[] registers;
    public byte[] flagRegister;

    public SpecialRegisters () {
        registers = new short[16];
        flagRegister = new byte[16];
    }

    public short getValue(String name){
        switch (name){
            case "cb": return registers[1];
            case "cl": return registers[2];
            case "cc": return registers[3];
            case "sb": return registers[4];
            case "sl": return registers[5];
            case "sc": return registers[6];
            case "db": return registers[7];
            case "dl": return registers[8];
            case "dc": return registers[9];
            case "pc": return registers[10];
            default: return 0;
        }
    }

    public void setValue(String name, short value){
        switch (name){
            case "cb": registers[1] = value;break;
            case "cl": registers[2] = value;break;
            case "cc": registers[3] = value;break;
            case "sb": registers[4] = value;break;
            case "sl": registers[5] = value;break;
            case "sc": registers[6] = value;break;
            case "db": registers[7] = value;break;
            case "dl": registers[8] = value;break;
            case "dc": registers[9] = value;break;
            case "pc": registers[10] = value;break;
        }
    }
    public short getCodeBase() {
        return registers[1];
    }

    public short getCodeLimit() {
        return registers[2];
    }

    public short getCodeCounter() {
        return registers[3];
    }

    public short getStackBase() {
        return registers[4];
    }

    public short getStackLimit() {
        return registers[5];
    }

    public short getStackCounter() {
        return registers[6];
    }

    public short getDataBase() {
        return registers[7];
    }

    public short getDataLimit() {
        return registers[8];
    }

    public short getDataCounter() {
        return registers[9];
    }

    public short getProgramCounter() {
        return registers[10];
    }

    /*
    In the flag register only indices 0-3 denote something meaningful.
    0 -> Carry
    1 -> Zero
    2 -> Sign
    3 -> Overflow
     */

    public short getFlagByValues(String flagName){
        switch (flagName){
            case "cf": return flagRegister[0];
            case "zf": return flagRegister[1];
            case "sf": return flagRegister[2];
            default: return 0;
        }
    }
    public short getCarryFlag() {
//        short flagRegister = registers[9];
//        short position = 0;
//        return getBitN.getBit(flagRegister, position);
        return flagRegister[0];
    }

    public short getZeroFlag() {
        return flagRegister[1];
    }

    public short getSignFlag() {
        return flagRegister[2];
    }

    public short getOverflowFlag() {
        return flagRegister[3];
    }

    public void setCodeBase(short value) {
        registers[1] = value;
    }

    public void setCodeLimit(short value) {
        registers[2] = value;
    }

    public void setCodeCounter(short value) {
        registers[3] = value;
    }

    public void setStackBase(short value) {
        registers[4] = value;
    }

    public void setStackLimit(short value) {
        registers[5] = value;
    }

    public void setStackCounter(short value) {
        registers[6] = value;
    }

    public void setDataBase(short value) {
        registers[7] = value;
    }

    public void setDataLimit(short value) {
        registers[8] = value;
    }

    public void setDataCounter(short value) {
        registers[9] = value;
    }

    public void setProgramCounter(int value) {
        registers[10] = (short) value;
    }

    /*
    To set the flag on or off we need to set the bit value to 1 or 0 at the bit position corresponding
    to the flag.
     */
    public void setCarryOn() {
//        short flagRegister = registers[9];
//        short position = 0;
//        if(getBitN.getBit(flagRegister, position) == 0) {
//            registers[9] += 1;
//        }
        //     01011100
        //   if(flagRegister & 00000100 == 00000100)
        // flagRegister | 00001000
        flagRegister[0] = 1;
    }

    public void setZeroOn() {
        flagRegister[1] = 1;
    }

    public void setSignOn() {
        flagRegister[2] = 1;
    }

    public void setOverflowOn() {
        flagRegister[3] = 1;
    }

    public void setCarryOff() {
//        short flagRegister = registers[9];
//        short position = 0;
//        if(getBitN.getBit(flagRegister, position) == 1) {
//            registers[9] -= 1;
//        }
        flagRegister[0] = 0;
    }

    public void setZeroOff() {
        flagRegister[1] = 0;
    }

    public void setSignOff() {
        flagRegister[2] = 0;
    }

    public void setOverflowOff() {
        flagRegister[3] = 0;
    }

    public void showFlags() {
        String flagString = "Carry: "+flagRegister[0]+" Zero: "+flagRegister[1]+" Sign: "+flagRegister[2]+" Overflow: "+flagRegister[3];
        System.out.println(flagString);
    }

    public String toString(){
      return "the priority is"+  this.registers[1] +
      " \n the code base  is "+   this.registers[2] + 
      " \n the code limit  is "+   this.registers[3] + 
      " \n the code counter is "+  this.registers[4] + 
      " \n the data base  is "+  this.registers[5] + 
      " \n the data limit is "+  this.registers[6] + 
      " \n the data counter  is "+  this.registers[7] + 
      " \n the stack base  is "+  this.registers[8] + 
      " \n the stack limit  is "+  this.registers[9] + 
      " \n the stack counter  is "+  this.registers[10]+ 
      " \n the Zero flag    is "+  this.flagRegister[1] 
+      " \n the Sign flag is "+  this.flagRegister[2] + 
      " \n the Overflow  is "+  this.flagRegister[3] 
      ;
    }
}
