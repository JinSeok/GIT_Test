/* autogenerated by gensyscalls.py */
#include <sys/linux-syscalls.h>

    .text
    .type sched_get_priority_min, @function
    .globl sched_get_priority_min
    .align 4

sched_get_priority_min:

    /* invoke trap */
    mov.l   0f, r3  /* trap num */
    trapa   #(1 + 0x10)

    /* check return value */
    cmp/pz  r0
    bt      __NR_sched_get_priority_min_end

    /* keep error number */
    sts.l   pr, @-r15
    mov.l   1f, r1
    jsr     @r1
    mov     r0, r4
    lds.l   @r15+, pr

__NR_sched_get_priority_min_end:
    rts
    nop

    .align  2
0:  .long   __NR_sched_get_priority_min
1:  .long   __set_syscall_errno
