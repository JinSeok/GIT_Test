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
#ifndef __ASM_SH64_POSIX_TYPES_H
#define __ASM_SH64_POSIX_TYPES_H

typedef unsigned long __kernel_ino_t;
typedef unsigned short __kernel_mode_t;
typedef unsigned short __kernel_nlink_t;
typedef long __kernel_off_t;
typedef int __kernel_pid_t;
typedef unsigned short __kernel_ipc_pid_t;
typedef unsigned short __kernel_uid_t;
typedef unsigned short __kernel_gid_t;
typedef long unsigned int __kernel_size_t;
typedef int __kernel_ssize_t;
typedef int __kernel_ptrdiff_t;
typedef long __kernel_time_t;
typedef long __kernel_suseconds_t;
typedef long __kernel_clock_t;
typedef int __kernel_timer_t;
typedef int __kernel_clockid_t;
typedef int __kernel_daddr_t;
typedef char * __kernel_caddr_t;
typedef unsigned short __kernel_uid16_t;
typedef unsigned short __kernel_gid16_t;
typedef unsigned int __kernel_uid32_t;
typedef unsigned int __kernel_gid32_t;

typedef unsigned short __kernel_old_uid_t;
typedef unsigned short __kernel_old_gid_t;
typedef unsigned short __kernel_old_dev_t;

#ifdef __GNUC__
typedef long long __kernel_loff_t;
#endif

typedef struct {
#ifdef __USE_ALL
 int val[2];
#else
 int __val[2];
#endif
} __kernel_fsid_t;

#if !defined(__GLIBC__) || __GLIBC__ < 2

#undef __FD_SET
static __inline__ void __FD_SET(unsigned long __fd, __kernel_fd_set *__fdsetp)
{
 unsigned long __tmp = __fd / __NFDBITS;
 unsigned long __rem = __fd % __NFDBITS;
 __fdsetp->fds_bits[__tmp] |= (1UL<<__rem);
}

#undef __FD_CLR
static __inline__ void __FD_CLR(unsigned long __fd, __kernel_fd_set *__fdsetp)
{
 unsigned long __tmp = __fd / __NFDBITS;
 unsigned long __rem = __fd % __NFDBITS;
 __fdsetp->fds_bits[__tmp] &= ~(1UL<<__rem);
}

#undef __FD_ISSET
static __inline__ int __FD_ISSET(unsigned long __fd, const __kernel_fd_set *__p)
{
 unsigned long __tmp = __fd / __NFDBITS;
 unsigned long __rem = __fd % __NFDBITS;
 return (__p->fds_bits[__tmp] & (1UL<<__rem)) != 0;
}

#undef __FD_ZERO
static __inline__ void __FD_ZERO(__kernel_fd_set *__p)
{
 unsigned long *__tmp = __p->fds_bits;
 int __i;

 if (__builtin_constant_p(__FDSET_LONGS)) {
 switch (__FDSET_LONGS) {
 case 16:
 __tmp[ 0] = 0; __tmp[ 1] = 0;
 __tmp[ 2] = 0; __tmp[ 3] = 0;
 __tmp[ 4] = 0; __tmp[ 5] = 0;
 __tmp[ 6] = 0; __tmp[ 7] = 0;
 __tmp[ 8] = 0; __tmp[ 9] = 0;
 __tmp[10] = 0; __tmp[11] = 0;
 __tmp[12] = 0; __tmp[13] = 0;
 __tmp[14] = 0; __tmp[15] = 0;
 return;

 case 8:
 __tmp[ 0] = 0; __tmp[ 1] = 0;
 __tmp[ 2] = 0; __tmp[ 3] = 0;
 __tmp[ 4] = 0; __tmp[ 5] = 0;
 __tmp[ 6] = 0; __tmp[ 7] = 0;
 return;

 case 4:
 __tmp[ 0] = 0; __tmp[ 1] = 0;
 __tmp[ 2] = 0; __tmp[ 3] = 0;
 return;
 }
 }
 __i = __FDSET_LONGS;
 while (__i) {
 __i--;
 *__tmp = 0;
 __tmp++;
 }
}

#endif

#endif
