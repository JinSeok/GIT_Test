/* autogenerated by gensyscalls.py */
#include <sys/linux-syscalls.h>

    .text
    .type mkdir, #function
    .globl mkdir
    .align 4
    .fnstart

mkdir:
    .save   {r4, r7}
    stmfd   sp!, {r4, r7}
    ldr     r7, =__NR_mkdir
    swi     #0
    ldmfd   sp!, {r4, r7}
    movs    r0, r0
    bxpl    lr
    b       __set_syscall_errno
    .fnend
