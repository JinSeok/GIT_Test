/* autogenerated by gensyscalls.py */
#include <sys/linux-syscalls.h>

    .text
    .type __llseek, #function
    .globl __llseek
    .align 4
    .fnstart

__llseek:
    mov     ip, sp
    .save   {r4, r5, r6, r7}
    stmfd   sp!, {r4, r5, r6, r7}
    ldmfd   ip, {r4, r5, r6}
    ldr     r7, =__NR__llseek
    swi     #0
    ldmfd   sp!, {r4, r5, r6, r7}
    movs    r0, r0
    bxpl    lr
    b       __set_syscall_errno
    .fnend
