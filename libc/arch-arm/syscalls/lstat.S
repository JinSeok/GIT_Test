/* autogenerated by gensyscalls.py */
#include <sys/linux-syscalls.h>

    .text
    .type lstat, #function
    .globl lstat
    .align 4
    .fnstart

lstat:
    .save   {r4, r7}
    stmfd   sp!, {r4, r7}
    ldr     r7, =__NR_lstat64
    swi     #0
    ldmfd   sp!, {r4, r7}
    movs    r0, r0
    bxpl    lr
    b       __set_syscall_errno
    .fnend
