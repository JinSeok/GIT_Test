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
#ifndef _IPT_TOS_H
#define _IPT_TOS_H

struct ipt_tos_info {
 u_int8_t tos;
 u_int8_t invert;
};

#ifndef IPTOS_NORMALSVC
#define IPTOS_NORMALSVC 0
#endif

#endif
