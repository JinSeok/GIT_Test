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
#ifndef _LINUX_ATMAPI_H
#define _LINUX_ATMAPI_H

#if defined(__sparc__) || defined(__ia64__)

#define __ATM_API_ALIGN __attribute__((aligned(8)))
#else
#define __ATM_API_ALIGN
#endif

typedef struct { unsigned char _[8]; } __ATM_API_ALIGN atm_kptr_t;

#endif
