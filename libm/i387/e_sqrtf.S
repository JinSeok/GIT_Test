/*
 * Written by J.T. Conklin <jtc@netbsd.org>.
 * Public domain.
 */

#include <machine/asm.h>

__FBSDID("$FreeBSD: src/lib/msun/i387/e_sqrtf.S,v 1.2 2005/02/04 14:08:32 das Exp $");
/* RCSID("$NetBSD: e_sqrtf.S,v 1.2 1995/05/08 23:50:14 jtc Exp $") */

ENTRY(sqrtf)
	flds	4(%esp)
	fsqrt
	ret
