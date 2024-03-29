#define STRLEN sse2_strlen_atom

#ifndef L
# define L(label)	.L##label
#endif

#ifndef cfi_startproc
# define cfi_startproc			.cfi_startproc
#endif

#ifndef cfi_endproc
# define cfi_endproc			.cfi_endproc
#endif

#ifndef cfi_rel_offset
# define cfi_rel_offset(reg, off)	.cfi_rel_offset reg, off
#endif

#ifndef cfi_restore
# define cfi_restore(reg)		.cfi_restore reg
#endif

#ifndef cfi_adjust_cfa_offset
# define cfi_adjust_cfa_offset(off)	.cfi_adjust_cfa_offset off
#endif

#ifndef cfi_remember_state
# define cfi_remember_state		.cfi_remember_state
#endif

#ifndef cfi_restore_state
# define cfi_restore_state		.cfi_restore_state
#endif

#ifndef ENTRY
# define ENTRY(name)			\
	.type name,  @function; 	\
	.globl name;			\
	.p2align 4;			\
name:					\
	cfi_startproc
#endif

#ifndef END
# define END(name)			\
	cfi_endproc;			\
	.size name, .-name
#endif

#define CFI_PUSH(REG)						\
  cfi_adjust_cfa_offset (4);					\
  cfi_rel_offset (REG, 0)

#define CFI_POP(REG)						\
  cfi_adjust_cfa_offset (-4);					\
  cfi_restore (REG)

#define PUSH(REG)	pushl REG; CFI_PUSH (REG)
#define POP(REG)	popl REG; CFI_POP (REG)
#define PARMS		4
#define	STR		PARMS
#define ENTRANCE
#define RETURN		ret

	.text
ENTRY (STRLEN)
	ENTRANCE
	mov	STR(%esp), %edx
	xor	%eax, %eax
	cmpb	$0, (%edx)
	jz	L(exit_tail0)
	cmpb	$0, 1(%edx)
	jz	L(exit_tail1)
	cmpb	$0, 2(%edx)
	jz	L(exit_tail2)
	cmpb	$0, 3(%edx)
	jz	L(exit_tail3)
	cmpb	$0, 4(%edx)
	jz	L(exit_tail4)
	cmpb	$0, 5(%edx)
	jz	L(exit_tail5)
	cmpb	$0, 6(%edx)
	jz	L(exit_tail6)
	cmpb	$0, 7(%edx)
	jz	L(exit_tail7)
	cmpb	$0, 8(%edx)
	jz	L(exit_tail8)
	cmpb	$0, 9(%edx)
	jz	L(exit_tail9)
	cmpb	$0, 10(%edx)
	jz	L(exit_tail10)
	cmpb	$0, 11(%edx)
	jz	L(exit_tail11)
	cmpb	$0, 12(%edx)
	jz	L(exit_tail12)
	cmpb	$0, 13(%edx)
	jz	L(exit_tail13)
	cmpb	$0, 14(%edx)
	jz	L(exit_tail14)
	cmpb	$0, 15(%edx)
	jz	L(exit_tail15)
	pxor	%xmm0, %xmm0
	mov	%edx, %eax
	mov	%edx, %ecx
	and	$-16, %eax
	add	$16, %ecx
	add	$16, %eax

	pcmpeqb	(%eax), %xmm0
	pmovmskb %xmm0, %edx
	pxor	%xmm1, %xmm1
	test	%edx, %edx
	lea	16(%eax), %eax
	jnz	L(exit)

	pcmpeqb	(%eax), %xmm1
	pmovmskb %xmm1, %edx
	pxor	%xmm2, %xmm2
	test	%edx, %edx
	lea	16(%eax), %eax
	jnz	L(exit)


	pcmpeqb	(%eax), %xmm2
	pmovmskb %xmm2, %edx
	pxor	%xmm3, %xmm3
	test	%edx, %edx
	lea	16(%eax), %eax
	jnz	L(exit)

	pcmpeqb	(%eax), %xmm3
	pmovmskb %xmm3, %edx
	test	%edx, %edx
	lea	16(%eax), %eax
	jnz	L(exit)

	pcmpeqb	(%eax), %xmm0
	pmovmskb %xmm0, %edx
	test	%edx, %edx
	lea	16(%eax), %eax
	jnz	L(exit)

	pcmpeqb	(%eax), %xmm1
	pmovmskb %xmm1, %edx
	test	%edx, %edx
	lea	16(%eax), %eax
	jnz	L(exit)

	pcmpeqb	(%eax), %xmm2
	pmovmskb %xmm2, %edx
	test	%edx, %edx
	lea	16(%eax), %eax
	jnz	L(exit)

	pcmpeqb	(%eax), %xmm3
	pmovmskb %xmm3, %edx
	test	%edx, %edx
	lea	16(%eax), %eax
	jnz	L(exit)

	pcmpeqb	(%eax), %xmm0
	pmovmskb %xmm0, %edx
	test	%edx, %edx
	lea	16(%eax), %eax
	jnz	L(exit)

	pcmpeqb	(%eax), %xmm1
	pmovmskb %xmm1, %edx
	test	%edx, %edx
	lea	16(%eax), %eax
	jnz	L(exit)

	pcmpeqb	(%eax), %xmm2
	pmovmskb %xmm2, %edx
	test	%edx, %edx
	lea	16(%eax), %eax
	jnz	L(exit)

	pcmpeqb	(%eax), %xmm3
	pmovmskb %xmm3, %edx
	test	%edx, %edx
	lea	16(%eax), %eax
	jnz	L(exit)

	pcmpeqb	(%eax), %xmm0
	pmovmskb %xmm0, %edx
	test	%edx, %edx
	lea	16(%eax), %eax
	jnz	L(exit)

	pcmpeqb	(%eax), %xmm1
	pmovmskb %xmm1, %edx
	test	%edx, %edx
	lea	16(%eax), %eax
	jnz	L(exit)

	pcmpeqb	(%eax), %xmm2
	pmovmskb %xmm2, %edx
	test	%edx, %edx
	lea	16(%eax), %eax
	jnz	L(exit)

	pcmpeqb	(%eax), %xmm3
	pmovmskb %xmm3, %edx
	test	%edx, %edx
	lea	16(%eax), %eax
	jnz	L(exit)

	and	$-0x40, %eax
	PUSH (%esi)
	PUSH (%edi)
	PUSH (%ebx)
	PUSH (%ebp)
	xor	%ebp, %ebp
L(aligned_64):
	pcmpeqb	(%eax), %xmm0
	pcmpeqb	16(%eax), %xmm1
	pcmpeqb	32(%eax), %xmm2
	pcmpeqb	48(%eax), %xmm3
	pmovmskb %xmm0, %edx
	pmovmskb %xmm1, %esi
	pmovmskb %xmm2, %edi
	pmovmskb %xmm3, %ebx
	or	%edx, %ebp
	or	%esi, %ebp
	or	%edi, %ebp
	or	%ebx, %ebp
	lea	64(%eax), %eax
	jz	L(aligned_64)
L(48leave):
	test	%edx, %edx
	jnz	L(aligned_64_exit_16)
	test	%esi, %esi
	jnz	L(aligned_64_exit_32)
	test	%edi, %edi
	jnz	L(aligned_64_exit_48)
	mov	%ebx, %edx
	lea	(%eax), %eax
	jmp	L(aligned_64_exit)
L(aligned_64_exit_48):
	lea	-16(%eax), %eax
	mov	%edi, %edx
	jmp	L(aligned_64_exit)
L(aligned_64_exit_32):
	lea	-32(%eax), %eax
	mov	%esi, %edx
	jmp	L(aligned_64_exit)
L(aligned_64_exit_16):
	lea	-48(%eax), %eax
L(aligned_64_exit):
	POP (%ebp)
	POP (%ebx)
	POP (%edi)
	POP (%esi)
L(exit):
	sub	%ecx, %eax
	test	%dl, %dl
	jz	L(exit_high)
	test	$0x01, %dl
	jnz	L(exit_tail0)

	test	$0x02, %dl
	jnz	L(exit_tail1)

	test	$0x04, %dl
	jnz	L(exit_tail2)

	test	$0x08, %dl
	jnz	L(exit_tail3)

	test	$0x10, %dl
	jnz	L(exit_tail4)

	test	$0x20, %dl
	jnz	L(exit_tail5)

	test	$0x40, %dl
	jnz	L(exit_tail6)
	add	$7, %eax
L(exit_tail0):
	RETURN

L(exit_high):
	add	$8, %eax
	test	$0x01, %dh
	jnz	L(exit_tail0)

	test	$0x02, %dh
	jnz	L(exit_tail1)

	test	$0x04, %dh
	jnz	L(exit_tail2)

	test	$0x08, %dh
	jnz	L(exit_tail3)

	test	$0x10, %dh
	jnz	L(exit_tail4)

	test	$0x20, %dh
	jnz	L(exit_tail5)

	test	$0x40, %dh
	jnz	L(exit_tail6)
	add	$7, %eax
	RETURN

	.p2align 4
L(exit_tail1):
	add	$1, %eax
	RETURN

L(exit_tail2):
	add	$2, %eax
	RETURN

L(exit_tail3):
	add	$3, %eax
	RETURN

L(exit_tail4):
	add	$4, %eax
	RETURN

L(exit_tail5):
	add	$5, %eax
	RETURN

L(exit_tail6):
	add	$6, %eax
	RETURN

L(exit_tail7):
	add	$7, %eax
	RETURN

L(exit_tail8):
	add	$8, %eax
	RETURN

L(exit_tail9):
	add	$9, %eax
	RETURN

L(exit_tail10):
	add	$10, %eax
	RETURN

L(exit_tail11):
	add	$11, %eax
	RETURN

L(exit_tail12):
	add	$12, %eax
	RETURN

L(exit_tail13):
	add	$13, %eax
	RETURN

L(exit_tail14):
	add	$14, %eax
	RETURN

L(exit_tail15):
	add	$15, %eax
	ret

END (STRLEN)
