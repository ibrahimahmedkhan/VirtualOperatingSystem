public class Execute {
    short[] gpr;
    SpecialRegisters spr;
    PCB pcb;

    public Execute(short[] gpr, SpecialRegisters spr, PCB pcb) {
        this.gpr = gpr;
        this.spr = spr;
        this.pcb = pcb;
    }

    public void execInstructions(byte[] memory, int i) {
        spr = pcb.spr;
        pcb.getInstructionFromMemory(memory, spr.getProgramCounter());
        short opCode;
        System.out.println("pcb get" + pcb.getInstructionFromMemory(memory, 0));
        short imm = 0;
        byte r1 = 0;
        byte r2 = 0;
        int iterations = 0;
        while (iterations < i) {
            System.out.println("program counter " + spr.getProgramCounter());
            iterations++;
            opCode = pcb.getInstructionFromMemory(memory, spr.getProgramCounter());
            System.out.println("opcode" + opCode);
            if (opCode == -13) {
                System.out.println("End of program");
                Paging.emptyAllProcessData(memory, PagingDemo.frameQueue, PCB.pageSize, pcb.dataPagingTable);
                Paging.emptyAllProcessData(memory, PagingDemo.frameQueue, PCB.pageSize, pcb.codePagingTable);
            }
            if (opCode >= 0x16 && opCode <= 0x1c) {
                r1 = pcb.getInstructionFromMemory(memory, spr.getProgramCounter() + 1);
                r2 = pcb.getInstructionFromMemory(memory, spr.getProgramCounter() + 2);
                spr.setProgramCounter(spr.getProgramCounter() + (short) 3);
            }

            if (opCode >= 0x30 && opCode <= 0x36) {
                r1 = pcb.getInstructionFromMemory(memory, spr.getProgramCounter() + 1);

                // it combines the two operands for immediate by bit shifting the left one by 8 bits leftward then performing an or with the right one after converting it to an unsigned integer
                imm = (short) ((pcb.getInstructionFromMemory(memory, spr.getProgramCounter() + 2) << 8 | ((memory[spr.getProgramCounter() + 3]) & 0xff)));
                spr.setProgramCounter(spr.getProgramCounter() + 4);
            }

            if (opCode >= 0x37 && opCode <= 0x3d) {
                imm = (short) ((pcb.getInstructionFromMemory(memory, spr.getProgramCounter() + 2) << 8 | ((pcb.getInstructionFromMemory(memory, spr.getProgramCounter() + 3)) & 0xff)));
                spr.setProgramCounter(spr.getProgramCounter() + 4);
            }


            if (opCode >= 0x51 && opCode <= 0x52) {
                r1 = pcb.getInstructionFromMemory(memory, spr.getProgramCounter() + 1);
                imm = (short) ((pcb.getInstructionFromMemory(memory, spr.getProgramCounter() + 2) << 8 | ((pcb.getInstructionFromMemory(memory, spr.getProgramCounter() + 3)))));
                spr.setProgramCounter(spr.getProgramCounter() + 4);
            }

            if (opCode >= 0x71 && opCode <= 0x78) {
                r1 = pcb.getInstructionFromMemory(memory, spr.getProgramCounter() + 1);
                spr.setProgramCounter(spr.getProgramCounter() + 2);
            }

            //set program counter after pushing from stack
            if (opCode == -15) {
                pcb.pushOnStack(memory, (byte) spr.getValue("pc"));
                spr.setProgramCounter(imm);
                break;
            }

            //in case of NOOP
            if (opCode == -14) {
                spr.setProgramCounter(spr.getProgramCounter() + 1);
                continue;
            }


            System.out.println(opCode + " " + r1 + " " + r2 + " " + imm + " ");

            switch (opCode) {
                case 0x16: {
                    gpr[r1] = gpr[r2];
                    break;
                }

                //ADD instruction R1 <- R1 + R2
                case 0x17: {
                    short r1Value = gpr[r1];
                    gpr[r1] = (short) (gpr[r1] + gpr[r2]);
          /*
          Turn Overflow ON when overflow occurs.
          Overflow in addition occurs when two +ve + +ve = -ve or -ve + -ve = +ve
          */
                    if (gpr[r1] + gpr[r2] == 0) spr.setZeroOn();
                    if ((r1Value > 0 && gpr[r2] > 0 && gpr[r1] < 0) || (r1Value < 0 && gpr[r2] < 0 && gpr[r1] > 0)) {
                        //System.out.println("Overflow Flag ON");
                        spr.setOverflowOn();
                    } else {
                        spr.setOverflowOff();
                    }
                    break;
                }

                //SUB instruction R1 <- R1 + R2
                case 0x18: {
                    short r1Value = gpr[r1];
                    gpr[r1] = (short) (gpr[r1] - gpr[r2]);
        /*
        Turn Overflow ON when overflow occurs.
        Overflow in subtraction occurs when  +ve - -ve = -ve or -ve - +ve = +ve
        */
                    if (gpr[r1] - gpr[r2] == 0) spr.setZeroOn();
                    if ((r1Value > 0 && gpr[r2] < 0 && gpr[r1] < 0) || (r1Value < 0 && gpr[r2] > 0 && gpr[r1] > 0)) {
                        //System.out.println("Overflow Flag ON");
                        spr.setOverflowOn();
                    } else {
                        spr.setOverflowOff();
                    }
                    break;
                }

                //MULTIPLY instruction R1 <- R1 * R2
                case 0x19: {
                    short r1Value = gpr[r1];
                    System.out.println(gpr[r1] + " " + gpr[r2]);
                    System.out.println((short) (gpr[r1] * gpr[r2]));
                    gpr[r1] = (short) (gpr[r1] * gpr[r2]);
                    if (gpr[r1] * gpr[r2] == 0) spr.setZeroOn();
                    if (r1Value != 0 && gpr[r1] / r1Value != gpr[r2]) {
                        //System.out.println("Overflow Flag ON");
                        spr.setOverflowOn();
                    } else {
                        spr.setOverflowOff();
                    }
                    break;
                }

                //DIVIDE instruction R1 <- R1 / R2
                case 0x1a: {
                    if (gpr[r2] == 0) spr.setOverflowOn();
                    else if (gpr[r1] / gpr[r2] == 0) spr.setZeroOn();
                    else gpr[r1] = (short) (gpr[r1] / gpr[r2]);
                    break;
                }

                //LOGICAL AND instruction R1 <- R1 && R2
                case 0x1b: {
                    if ((gpr[r1] & gpr[r2]) == 0) spr.setZeroOn();
                    gpr[r1] = (short) (short) (gpr[r1] > 0 && gpr[r2] > 0 ? 1 : 0);
                    break;
                }

                //LOGICAL OR instruction R1 <- R1 || R2
                case 0x1c: {
                    if ((gpr[r1] | gpr[r2]) == 0) spr.setZeroOn();
                    gpr[r1] = (short) (gpr[r1] > 0 || gpr[r2] > 0 ? 1 : 0);
                    break;
                }

                //MOVI instruction R1 <- imm
                case 0x30: {
                    gpr[r1] = imm;
                    break;
                }

                //ADDI instruction R1 <- R1 + imm
                case 0x31: {
                    short r1Value = gpr[r1];
                    gpr[r1] = (short) (gpr[r1] + imm);

        /*
        Turn Overflow ON when overflow occurs.
        Overflow in addition occurs when two +ve + +ve = -ve or -ve + -ve = +ve
        */
                    if ((gpr[r1] + gpr[r2]) == 0) spr.setZeroOn();
                    if ((r1Value > 0 && imm > 0 && gpr[r1] < 0) || (r1Value < 0 && imm < 0 && gpr[r1] > 0)) {
                        //System.out.println("Overflow Flag ON");
                        spr.setOverflowOn();
                    } else {
                        spr.setOverflowOff();
                    }
                    break;
                }

                //SUBI instruction R1 <- R1 - imm
                case 0x32: {
                    short r1Value = gpr[r1];
                    gpr[r1] = (short) (gpr[r1] - imm);

        /*
        Turn Overflow ON when overflow occurs.
        Overflow in subtraction occurs when  +ve - -ve = -ve or -ve - +ve = +ve
        */
                    if ((r1Value > 0 && imm < 0 && gpr[r1] < 0) || (r1Value < 0 && imm > 0 && gpr[r1] > 0)) {
                        System.out.println("Overflow Flag ON");
                        spr.setOverflowOn();
                    } else {
                        spr.setOverflowOff();
                    }
                    break;
                }

                //MULI instruction R1 <- R1 * imm
                case 0x33: {
                    short r1Value = gpr[r1];
                    gpr[r1] = (short) (gpr[r1] * imm);

                    if (r1Value != 0 && gpr[r1] / r1Value != imm) {
                        System.out.println("Overflow Flag ON");
                        spr.setOverflowOn();
                    } else {
                        spr.setOverflowOff();
                    }
                    break;
                }

                //DIVI instruction R1 <- R1 / imm
                case 0x34: {
                    if (imm == 0) {
                        System.out.println("Overflow Flag ON");
                        spr.setOverflowOn();
                    } else {
                        spr.setOverflowOff();
                    }

                    gpr[r1] = (short) (gpr[r1] / imm);
                    break;
                }

                //LOGICAL ANDI instruction
                case 0x35: {
                    gpr[r1] = (short) (gpr[r1] > 0 && imm > 0 ? 1 : 0);
                    break;
                }

                //LOGICAL ORI instruction
                case 0x36: {
                    gpr[r1] = (short) (gpr[r1] > 0 || imm > 0 ? 1 : 0);
                    break;
                }

                //BRANCH if zero
                case 0x37: {
                    if (spr.getZeroFlag() == 1) {
                        spr.setProgramCounter((short) (spr.getCodeBase() + imm));
                    }
                    break;
                }

                //BRANCH if not zero
                case 0x38: {
                    if (spr.getZeroFlag() != 1) {
                        spr.setProgramCounter((short) (spr.getCodeBase() + imm));
                    }
                    break;
                }

                //BRANCH if carry
                case 0x39: {
                    if (spr.getCarryFlag() == 1) {
                        spr.setProgramCounter((short) (spr.getCodeBase() + imm));
                    }
                    break;
                }

                //BRANCH if sign
                case 0x3a: {
                    if (spr.getSignFlag() == 1) {
                        spr.setProgramCounter((short) (spr.getCodeBase() + imm));
                    }
                    break;
                }

                //JUMP instruction
                case 0x3b: {
                    spr.setProgramCounter((short) (spr.getCodeBase() + imm));
                    break;
                }

                //CALL instruction
                case 0x3c: {
                    pcb.pushOnStack(memory, (byte) spr.getProgramCounter());
                    spr.setProgramCounter(22);
                }

                //ACT instruction
                case 0x3d: {
                    //TODO
                }

                //MOVL instruction
                case 0x51: {
                    gpr[r1] = (short) (pcb.getDataFromMemory(memory, imm) << 8);
                    gpr[r1] += (short) (pcb.getDataFromMemory(memory, imm + 1) & 0xFF);
                    // gpr[r1] = 2;
                    break;
                }
                //MOVS instruction
                case 0x52: {
                    pcb.saveDataToMemory(memory, (byte) (gpr[r1] >> 8), imm);
                    pcb.saveDataToMemory(memory, (byte) ((gpr[r1] << 8) >> 8), imm + 1);
                    break;
                }

                //Shift Left Logical
                case 0x71: {
                    gpr[r1] = (short) (gpr[r1] << 1);
                    break;
                }

                //Shift Right Logical
                case 0x72: {
                    gpr[r1] = (short) (gpr[r1] >> 1);
                    break;
                }

                //Rotate Left
                case 0x73: {
                    gpr[r1] = (short) ((short) (gpr[r1] << 1) | (short) ((short) (gpr[r1] & 0b1000000000000000) >> 15));
                    break;
                }


                //Rotate Right
                case 0x74: {
                    gpr[r1] = (short) ((short) (gpr[r1] >> 1) | (short) ((short) (gpr[r1] & 0b0000000000000001) << 15));
                    break;
                }

                //Increment
                case 0x75: {
                    short temp = gpr[r1];
                    gpr[r1] = (short) (gpr[r1] + 1);
                    if (gpr[r1] < temp) spr.setOverflowOn();
                    break;
                }

                //Decrement
                case 0x76: {
                    short temp = gpr[r1];
                    gpr[r1] = (short) (gpr[r1] - 1);
                    if (gpr[r1] > temp) spr.setOverflowOn();
                    break;
                }

                //push
                case 0x77: {
                    pcb.pushOnStack(memory, (byte) gpr[r1]);
                    break;
                }

                //pop
                case 0x78: {
                    gpr[r1] = pcb.popFromStack(memory);
                    break;
                }

                case 0x81: {

                    break;
                }
                default:
                    System.out.println("error");
            }

            showRegisters(gpr);
            spr.showFlags();
        }
    }


    public static void execIns(byte[] memory, short[] gpr, SpecialRegisters spr, int offset, PCB pcb, int i) {
        spr = pcb.spr;
        short opCode = pcb.getInstructionFromMemory(memory, spr.getProgramCounter());
        System.out.println("pcb get" + pcb.getInstructionFromMemory(memory, 0));
        short imm = 0;
        byte r1 = 0;
        byte r2 = 0;
        int iterations = 0;
        while (iterations < i) {
            System.out.println("program counter " + spr.getProgramCounter());
            iterations++;
            opCode = pcb.getInstructionFromMemory(memory, spr.getProgramCounter());
            System.out.println("opcode" + opCode);
            if (opCode == -13) {
                System.out.println("End of program");
                Paging.emptyAllProcessData(memory, PagingDemo.frameQueue, PCB.pageSize, pcb.dataPagingTable);
                Paging.emptyAllProcessData(memory, PagingDemo.frameQueue, PCB.pageSize, pcb.codePagingTable);
            }
            if (opCode >= 0x16 && opCode <= 0x1c) {
                r1 = pcb.getInstructionFromMemory(memory, spr.getProgramCounter() + 1);
                r2 = pcb.getInstructionFromMemory(memory, spr.getProgramCounter() + 2);
                spr.setProgramCounter(spr.getProgramCounter() + (short) 3);
            }

            if (opCode >= 0x30 && opCode <= 0x36) {
                r1 = pcb.getInstructionFromMemory(memory, spr.getProgramCounter() + 1);

                // it combines the two operands for immediate by bit shifting the left one by 8 bits leftward then performing an or with the right one after converting it to an unsigned integer
                imm = (short) ((pcb.getInstructionFromMemory(memory, spr.getProgramCounter() + 2) << 8 | ((memory[spr.getProgramCounter() + 3]) & 0xff)));
                spr.setProgramCounter(spr.getProgramCounter() + 4);
            }

            if (opCode >= 0x37 && opCode <= 0x3d) {
                imm = (short) ((pcb.getInstructionFromMemory(memory, spr.getProgramCounter() + 2) << 8 | ((pcb.getInstructionFromMemory(memory, spr.getProgramCounter() + 3)) & 0xff)));
                spr.setProgramCounter(spr.getProgramCounter() + 4);
            }


            if (opCode >= 0x51 && opCode <= 0x52) {
                r1 = pcb.getInstructionFromMemory(memory, spr.getProgramCounter() + 1);
                imm = (short) ((pcb.getInstructionFromMemory(memory, spr.getProgramCounter() + 2) << 8 | ((pcb.getInstructionFromMemory(memory, spr.getProgramCounter() + 3)))));
                spr.setProgramCounter(spr.getProgramCounter() + 4);
            }

            if (opCode >= 0x71 && opCode <= 0x78) {
                r1 = pcb.getInstructionFromMemory(memory, spr.getProgramCounter() + 1);
                spr.setProgramCounter(spr.getProgramCounter() + 2);
            }

            //set program counter after pushing from stack
            if (opCode == -15) {

            }

            //in case of NOOP
            if (opCode == -14) {
                spr.setProgramCounter(spr.getProgramCounter() + 1);
                continue;
            }


            System.out.println(opCode + " " + r1 + " " + r2 + " " + imm + " ");

            switch (opCode) {
                case 0x16: {
                    gpr[r1] = gpr[r2];
                    break;
                }

                //ADD instruction R1 <- R1 + R2
                case 0x17: {
                    short r1Value = gpr[r1];
                    gpr[r1] = (short) (gpr[r1] + gpr[r2]);
          /*
          Turn Overflow ON when overflow occurs.
          Overflow in addition occurs when two +ve + +ve = -ve or -ve + -ve = +ve
          */
                    if (gpr[r1] + gpr[r2] == 0) spr.setZeroOn();
                    if ((r1Value > 0 && gpr[r2] > 0 && gpr[r1] < 0) || (r1Value < 0 && gpr[r2] < 0 && gpr[r1] > 0)) {
                        //System.out.println("Overflow Flag ON");
                        spr.setOverflowOn();
                    } else {
                        spr.setOverflowOff();
                    }
                    break;
                }

                //SUB instruction R1 <- R1 + R2
                case 0x18: {
                    short r1Value = gpr[r1];
                    gpr[r1] = (short) (gpr[r1] - gpr[r2]);
        /*
        Turn Overflow ON when overflow occurs.
        Overflow in subtraction occurs when  +ve - -ve = -ve or -ve - +ve = +ve
        */
                    if (gpr[r1] - gpr[r2] == 0) spr.setZeroOn();
                    if ((r1Value > 0 && gpr[r2] < 0 && gpr[r1] < 0) || (r1Value < 0 && gpr[r2] > 0 && gpr[r1] > 0)) {
                        //System.out.println("Overflow Flag ON");
                        spr.setOverflowOn();
                    } else {
                        spr.setOverflowOff();
                    }
                    break;
                }

                //MULTIPLY instruction R1 <- R1 * R2
                case 0x19: {
                    short r1Value = gpr[r1];
                    System.out.println(gpr[r1] + " " + gpr[r2]);
                    System.out.println((short) (gpr[r1] * gpr[r2]));
                    gpr[r1] = (short) (gpr[r1] * gpr[r2]);
                    if (gpr[r1] * gpr[r2] == 0) spr.setZeroOn();
                    if (r1Value != 0 && gpr[r1] / r1Value != gpr[r2]) {
                        //System.out.println("Overflow Flag ON");
                        spr.setOverflowOn();
                    } else {
                        spr.setOverflowOff();
                    }
                    break;
                }

                //DIVIDE instruction R1 <- R1 / R2
                case 0x1a: {
                    if (gpr[r2] == 0) spr.setOverflowOn();
                    else if (gpr[r1] / gpr[r2] == 0) spr.setZeroOn();
                    else gpr[r1] = (short) (gpr[r1] / gpr[r2]);
                    break;
                }

                //LOGICAL AND instruction R1 <- R1 && R2
                case 0x1b: {
                    if ((gpr[r1] & gpr[r2]) == 0) spr.setZeroOn();
                    gpr[r1] = (short) (short) (gpr[r1] > 0 && gpr[r2] > 0 ? 1 : 0);
                    break;
                }

                //LOGICAL OR instruction R1 <- R1 || R2
                case 0x1c: {
                    if ((gpr[r1] | gpr[r2]) == 0) spr.setZeroOn();
                    gpr[r1] = (short) (gpr[r1] > 0 || gpr[r2] > 0 ? 1 : 0);
                    break;
                }

                //MOVI instruction R1 <- imm
                case 0x30: {
                    gpr[r1] = imm;
                    break;
                }

                //ADDI instruction R1 <- R1 + imm
                case 0x31: {
                    short r1Value = gpr[r1];
                    gpr[r1] = (short) (gpr[r1] + imm);

        /*
        Turn Overflow ON when overflow occurs.
        Overflow in addition occurs when two +ve + +ve = -ve or -ve + -ve = +ve
        */
                    if ((gpr[r1] + gpr[r2]) == 0) spr.setZeroOn();
                    if ((r1Value > 0 && imm > 0 && gpr[r1] < 0) || (r1Value < 0 && imm < 0 && gpr[r1] > 0)) {
                        //System.out.println("Overflow Flag ON");
                        spr.setOverflowOn();
                    } else {
                        spr.setOverflowOff();
                    }
                    break;
                }

                //SUBI instruction R1 <- R1 - imm
                case 0x32: {
                    short r1Value = gpr[r1];
                    gpr[r1] = (short) (gpr[r1] - imm);

        /*
        Turn Overflow ON when overflow occurs.
        Overflow in subtraction occurs when  +ve - -ve = -ve or -ve - +ve = +ve
        */
                    if ((r1Value > 0 && imm < 0 && gpr[r1] < 0) || (r1Value < 0 && imm > 0 && gpr[r1] > 0)) {
                        System.out.println("Overflow Flag ON");
                        spr.setOverflowOn();
                    } else {
                        spr.setOverflowOff();
                    }
                    break;
                }

                //MULI instruction R1 <- R1 * imm
                case 0x33: {
                    short r1Value = gpr[r1];
                    gpr[r1] = (short) (gpr[r1] * imm);

                    if (r1Value != 0 && gpr[r1] / r1Value != imm) {
                        System.out.println("Overflow Flag ON");
                        spr.setOverflowOn();
                    } else {
                        spr.setOverflowOff();
                    }
                    break;
                }

                //DIVI instruction R1 <- R1 / imm
                case 0x34: {
                    if (imm == 0) {
                        System.out.println("Overflow Flag ON");
                        spr.setOverflowOn();
                    } else {
                        spr.setOverflowOff();
                    }

                    gpr[r1] = (short) (gpr[r1] / imm);
                    break;
                }

                //LOGICAL ANDI instruction
                case 0x35: {
                    gpr[r1] = (short) (gpr[r1] > 0 && imm > 0 ? 1 : 0);
                    break;
                }

                //LOGICAL ORI instruction
                case 0x36: {
                    gpr[r1] = (short) (gpr[r1] > 0 || imm > 0 ? 1 : 0);
                    break;
                }

                //BRANCH if zero
                case 0x37: {
                    if (spr.getZeroFlag() == 1) {
                        spr.setProgramCounter((short) (spr.getCodeBase() + imm));
                    }
                    break;
                }

                //BRANCH if not zero
                case 0x38: {
                    if (spr.getZeroFlag() != 1) {
                        spr.setProgramCounter((short) (spr.getCodeBase() + imm));
                    }
                    break;
                }

                //BRANCH if carry
                case 0x39: {
                    if (spr.getCarryFlag() == 1) {
                        spr.setProgramCounter((short) (spr.getCodeBase() + imm));
                    }
                    break;
                }

                //BRANCH if sign
                case 0x3a: {
                    if (spr.getSignFlag() == 1) {
                        spr.setProgramCounter((short) (spr.getCodeBase() + imm));
                    }
                    break;
                }

                //JUMP instruction
                case 0x3b: {
                    spr.setProgramCounter((short) (spr.getCodeBase() + imm));
                    break;
                }

                //CALL instruction
                case 0x3c: {
                    pcb.pushOnStack(memory, (byte) spr.getProgramCounter());
                    spr.setProgramCounter(22);
                }

                //ACT instruction
                case 0x3d: {
                    //TODO
                }

                //MOVL instruction
                case 0x51: {
                    gpr[r1] = (short) (pcb.getDataFromMemory(memory, imm) << 8);
                    gpr[r1] += (short) (pcb.getDataFromMemory(memory, imm + 1) & 0xFF);
                    // gpr[r1] = 2;
                    break;
                }
                //MOVS instruction
                case 0x52: {
                    pcb.saveDataToMemory(memory, (byte) (gpr[r1] >> 8), imm);
                    pcb.saveDataToMemory(memory, (byte) ((gpr[r1] << 8) >> 8), imm + 1);
                    break;
                }

                //Shift Left Logical
                case 0x71: {
                    gpr[r1] = (short) (gpr[r1] << 1);
                    break;
                }

                //Shift Right Logical
                case 0x72: {
                    gpr[r1] = (short) (gpr[r1] >> 1);
                    break;
                }

                //Rotate Left
                case 0x73: {
                    gpr[r1] = (short) ((short) (gpr[r1] << 1) | (short) ((short) (gpr[r1] & 0b1000000000000000) >> 15));
                    break;
                }


                //Rotate Right
                case 0x74: {
                    gpr[r1] = (short) ((short) (gpr[r1] >> 1) | (short) ((short) (gpr[r1] & 0b0000000000000001) << 15));
                    break;
                }

                //Increment
                case 0x75: {
                    short temp = gpr[r1];
                    gpr[r1] = (short) (gpr[r1] + 1);
                    if (gpr[r1] < temp) spr.setOverflowOn();
                    break;
                }

                //Decrement
                case 0x76: {
                    short temp = gpr[r1];
                    gpr[r1] = (short) (gpr[r1] - 1);
                    if (gpr[r1] > temp) spr.setOverflowOn();
                    break;
                }

                //push
                case 0x77: {
                    pcb.pushOnStack(memory, (byte) gpr[r1]);
                    break;
                }

                //pop
                case 0x78: {
                    gpr[r1] = pcb.popFromStack(memory);
                    break;
                }

                case 0x81: {

                    break;
                }
                default:
                    System.out.println("error");
            }

            showRegisters(gpr);
            spr.showFlags();
        }
    }


    //DIVIDE instruction R1 <- R1 / R2
//    private void divide(){
//        if (gpr[r2] == 0) spr.setOverflowOn();
//        else if (gpr[r1] / gpr[r2] == 0) spr.setZeroOn();
//        else gpr[r1] = (short) (gpr[r1] / gpr[r2]);
//    }


    /*The showRegisters function displays the content of all the registers in the memory*/

    public static void showRegisters(short[] gpr) {
        for (short value : gpr) {
            System.out.print(value + " ");
        }
        System.out.println();
    }


}


