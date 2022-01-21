import java.util.Queue;


//this class will perform frame finding and paging operations
public class Paging<T> {

    //returns the first free frame from a given frame queue
    static Integer firstFreeFrame(Queue<Integer> frameQueue){
        return frameQueue.peek();

    }

    //removes the first free frame from a given queue and removes
    //it
    static Integer removeFreeFrame(Queue<Integer> frameQueue){
        return frameQueue.poll();
    }

    // finds first free frame, loads data into it, 
    // and returns the framenumber of this frame
    static int loadDataIntoFrame(Queue<Integer> frameQueue, byte[] memory, byte[] arr, int frameSize){
        int frameNum = removeFreeFrame(frameQueue);
        for(int i = 0; i < frameSize; i++){
            memory[frameNum * frameSize + i] = arr[i];
        }
        return frameNum;
    }

    //empties a given frame from memory, and adds it to the frame
    //queue
    static void emptyDataFromFrame(byte[] memory,Queue<Integer> frameQueue, int frameNum, int frameSize){
        for(int i = 0;i < frameSize; i++){
            memory[frameNum * frameSize + i] = 0;
        }
        frameQueue.add(frameNum);
    }

    //empties all the data for a process from memory, and adds
    //the free frames to the frame queue
    static void emptyAllProcessData(byte[] memory, Queue<Integer> frameQueue, int frameSize, int[] pagingTable){
        for(int i =0; i < pagingTable.length; i++){
            emptyDataFromFrame(memory, frameQueue, pagingTable[i], frameSize);
        }
    }
}
