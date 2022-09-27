// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/04/Fill.asm

// Runs an infinite loop that listens to the keyboard input.
// When a key is pressed (any key), the program blackens the screen,
// i.e. writes "black" in every pixel;
// the screen should remain fully black as long as the key is pressed. 
// When no key is pressed, the program clears the screen, i.e. writes
// "white" in every pixel;
// the screen should remain fully clear as long as no key is pressed.

// Put your code here.

@color
M=0

(loop)
    @SCREEN
    D=A

    @pixels 
    M=D

    @KBD
    D=M

    @black
    D;JGT

    @color
    M=0
    
    @color_screen
    0;JMP

    (black)
        @color
        M=-1

    (color_screen)
        @color
        D=M
        @pixels
        A=M
        M=D

        @pixels
        M=M+1
        D=M

        @24576
        D=D-A
        @color_screen
        D;JLT

@loop
0;JMP  