/* autogenerated by gensyscalls.py */
#include <sys/linux-syscalls.h>

    .text
    .type pwrite64, #function
    .globl pwrite64
    .align 4
    .fnstart

pwrite64:
    mov     ip, sp
    .save   {r4, r5, r6, r7}
    stmfd   sp!, {r4, r5, r6, r7}
    ldmfd   ip, {r4, r5, r6}
    ldr     r7, =__NR_pwrite64
    swi     #0
    ldmfd   sp!, {r4, r5, r6, r7}
    movs    r0, r0
    bxpl    lr
    b       __set_syscall_errno
    .fnend
