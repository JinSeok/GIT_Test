/* autogenerated by gensyscalls.py */
#include <sys/linux-syscalls.h>

    .text
    .type getgid, @function
    .globl getgid
    .align 4

getgid:
    movl    $__NR_getgid32, %eax
    int     $0x80
    cmpl    $-129, %eax
    jb      1f
    negl    %eax
    pushl   %eax
    call    __set_errno
    addl    $4, %esp
    orl     $-1, %eax
1:
    ret
