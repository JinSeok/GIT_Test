/* autogenerated by gensyscalls.py */
#include <sys/linux-syscalls.h>

    .text
    .type clock_getres, #function
    .globl clock_getres
    .align 4
    .fnstart

clock_getres:
    .save   {r4, r7}
    stmfd   sp!, {r4, r7}
    ldr     r7, =__NR_clock_getres
    swi     #0
    ldmfd   sp!, {r4, r7}
    movs    r0, r0
    bxpl    lr
    b       __set_syscall_errno
    .fnend
