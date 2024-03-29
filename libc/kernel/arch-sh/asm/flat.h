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
#ifndef __ASM_SH_FLAT_H
#define __ASM_SH_FLAT_H

#define flat_stack_align(sp)  
#define flat_argvp_envp_on_stack() 0
#define flat_old_ram_flag(flags) (flags)
#define flat_reloc_valid(reloc, size) ((reloc) <= (size))
#define flat_get_addr_from_rp(rp, relval, flags, p) get_unaligned(rp)
#define flat_put_addr_at_rp(rp, val, relval) put_unaligned(val,rp)
#define flat_get_relocate_addr(rel) (rel)
#define flat_set_persistent(relval, p) ({ (void)p; 0; })

#define FLAT_PLAT_INIT(_r)   do { _r->regs[0]=0; _r->regs[1]=0; _r->regs[2]=0; _r->regs[3]=0;   _r->regs[4]=0; _r->regs[5]=0; _r->regs[6]=0; _r->regs[7]=0;   _r->regs[8]=0; _r->regs[9]=0; _r->regs[10]=0; _r->regs[11]=0;   _r->regs[12]=0; _r->regs[13]=0; _r->regs[14]=0;   _r->sr = SR_FD; } while (0)

#endif
