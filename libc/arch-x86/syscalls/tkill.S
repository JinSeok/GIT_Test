/* autogenerated by gensyscalls.py */
#include <sys/linux-syscalls.h>

    .text
    .type tkill, @function
    .globl tkill
    .align 4

tkill:
    pushl   %ebx
    pushl   %ecx
    mov     12(%esp), %ebx
    mov     16(%esp), %ecx
    movl    $__NR_tkill, %eax
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
