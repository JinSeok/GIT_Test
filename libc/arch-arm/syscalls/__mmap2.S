/* autogenerated by gensyscalls.py */
#include <sys/linux-syscalls.h>

    .text
    .type __mmap2, #function
    .globl __mmap2
    .align 4
    .fnstart

__mmap2:
    mov     ip, sp
    .save   {r4, r5, r6, r7}
    stmfd   sp!, {r4, r5, r6, r7}
    ldmfd   ip, {r4, r5, r6}
    ldr     r7, =__NR_mmap2
    swi     #0
    ldmfd   sp!, {r4, r5, r6, r7}
    movs    r0, r0
    bxpl    lr
    b       __set_syscall_errno
    .fnend
