/****************************************************************************
 ****************************************************************************
 ***
 ***   This header was automatically generated from a Linux kernel header
 ***   of the same name, to make information necessary for userspace to
 ***   call into the kernel available to libc.  It contains only constants,
 ***   structures, and macros generated from the original header, and thus,
 ***   contains no copyrightable information.
 ***
 ****************************************************************************
 ****************************************************************************/
#ifndef _ASMARM_SIGNAL_H
#define _ASMARM_SIGNAL_H

#include <linux/types.h>

struct siginfo;

#define NSIG 32
typedef unsigned long sigset_t;

#define SIGHUP 1
#define SIGINT 2
#define SIGQUIT 3
#define SIGILL 4
#define SIGTRAP 5
#define SIGABRT 6
#define SIGIOT 6
#define SIGBUS 7
#define SIGFPE 8
#define SIGKILL 9
#define SIGUSR1 10
#define SIGSEGV 11
#define SIGUSR2 12
#define SIGPIPE 13
#define SIGALRM 14
#define SIGTERM 15
#define SIGSTKFLT 16
#define SIGCHLD 17
#define SIGCONT 18
#define SIGSTOP 19
#define SIGTSTP 20
#define SIGTTIN 21
#define SIGTTOU 22
#define SIGURG 23
#define SIGXCPU 24
#define SIGXFSZ 25
#define SIGVTALRM 26
#define SIGPROF 27
#define SIGWINCH 28
#define SIGIO 29
#define SIGPOLL SIGIO

#define SIGPWR 30
#define SIGSYS 31
#define SIGUNUSED 31

#define SIGRTMIN 32
#define SIGRTMAX _NSIG

#define SIGSWI 32

#define SA_NOCLDSTOP 0x00000001
#define SA_NOCLDWAIT 0x00000002
#define SA_SIGINFO 0x00000004
#define SA_THIRTYTWO 0x02000000
#define SA_RESTORER 0x04000000
#define SA_ONSTACK 0x08000000
#define SA_RESTART 0x10000000
#define SA_NODEFER 0x40000000
#define SA_RESETHAND 0x80000000

#define SA_NOMASK SA_NODEFER
#define SA_ONESHOT SA_RESETHAND

#define SS_ONSTACK 1
#define SS_DISABLE 2

#define MINSIGSTKSZ 2048
#define SIGSTKSZ 8192

#include <asm-generic/signal.h>

struct sigaction {
 union {
 __sighandler_t _sa_handler;
 void (*_sa_sigaction)(int, struct siginfo *, void *);
 } _u;
 sigset_t sa_mask;
 unsigned long sa_flags;
 void (*sa_restorer)(void);
};

#define sa_handler _u._sa_handler
#define sa_sigaction _u._sa_sigaction

typedef struct sigaltstack {
 void __user *ss_sp;
 int ss_flags;
 size_t ss_size;
} stack_t;

#endif
