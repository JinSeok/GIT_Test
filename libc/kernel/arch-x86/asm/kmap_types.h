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
#ifndef _ASM_X86_KMAP_TYPES_H
#define _ASM_X86_KMAP_TYPES_H

#define D(n)

enum km_type {
D(0) KM_BOUNCE_READ,
D(1) KM_SKB_SUNRPC_DATA,
D(2) KM_SKB_DATA_SOFTIRQ,
D(3) KM_USER0,
D(4) KM_USER1,
D(5) KM_BIO_SRC_IRQ,
D(6) KM_BIO_DST_IRQ,
D(7) KM_PTE0,
D(8) KM_PTE1,
D(9) KM_IRQ0,
D(10) KM_IRQ1,
D(11) KM_SOFTIRQ0,
D(12) KM_SOFTIRQ1,
D(13) KM_TYPE_NR
};

#undef D

#endif
