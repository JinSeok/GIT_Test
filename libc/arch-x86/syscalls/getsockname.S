/* autogenerated by gensyscalls.py */
#include <sys/linux-syscalls.h>

    .text
    .type getsockname, @function
    .globl getsockname
    .align 4

getsockname:
    pushl   %ebx
    pushl   %ecx
    mov     $6, %ebx
    mov     %esp, %ecx
    addl    $12, %ecx
    movl    $__NR_socketcall, %eax
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
