/* autogenerated by gensyscalls.py */
#include <sys/linux-syscalls.h>

    .text
    .type sigaltstack, @function
    .globl sigaltstack
    .align 4

sigaltstack:
    pushl   %ebx
    pushl   %ecx
    mov     12(%esp), %ebx
    mov     16(%esp), %ecx
    movl    $__NR_sigaltstack, %eax
    int     $0x80
    cmpl    $-129, %eax
    jb      1f
    negl    %eax
    pushl   %eax
    call    __set_errno
    addl    $4, %esp
    orl     $-1, %eax
1:
    popl    %ecx
    popl    %ebx
    ret
