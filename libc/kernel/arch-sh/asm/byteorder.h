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
#ifndef __ASM_SH_BYTEORDER_H
#define __ASM_SH_BYTEORDER_H

#include <linux/compiler.h>
#include <linux/types.h>

static inline __attribute_const__ __u32 ___arch__swab32(__u32 x)
{
 __asm__(
#ifdef __SH5__
 "byterev	%0, %0\n\t"
 "shari		%0, 32, %0"
#else
 "swap.b		%0, %0\n\t"
 "swap.w		%0, %0\n\t"
 "swap.b		%0, %0"
#endif
 : "=r" (x)
 : "0" (x));

 return x;
}

static inline __attribute_const__ __u16 ___arch__swab16(__u16 x)
{
 __asm__(
#ifdef __SH5__
 "byterev	%0, %0\n\t"
 "shari		%0, 32, %0"
#else
 "swap.b		%0, %0"
#endif
 : "=r" (x)
 : "0" (x));

 return x;
}

static inline __u64 ___arch__swab64(__u64 val)
{
 union {
 struct { __u32 a,b; } s;
 __u64 u;
 } v, w;
 v.u = val;
 w.s.b = ___arch__swab32(v.s.a);
 w.s.a = ___arch__swab32(v.s.b);
 return w.u;
}

#define __arch__swab64(x) ___arch__swab64(x)
#define __arch__swab32(x) ___arch__swab32(x)
#define __arch__swab16(x) ___arch__swab16(x)

#ifndef __STRICT_ANSI__
#define __BYTEORDER_HAS_U64__
#define __SWAB_64_THRU_32__
#endif

#ifdef __LITTLE_ENDIAN__
#include <linux/byteorder/little_endian.h>
#else
#include <linux/byteorder/big_endian.h>
#endif

#endif
