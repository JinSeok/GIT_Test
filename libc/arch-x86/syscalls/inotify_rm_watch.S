/* autogenerated by gensyscalls.py */
#include <sys/linux-syscalls.h>

    .text
    .type inotify_rm_watch, @function
    .globl inotify_rm_watch
    .align 4

inotify_rm_watch:
    pushl   %ebx
    pushl   %ecx
    mov     12(%esp), %ebx
    mov     16(%esp), %ecx
    movl    $__NR_inotify_rm_watch, %eax
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
