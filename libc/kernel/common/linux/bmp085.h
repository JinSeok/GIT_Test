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
#ifndef __BMP085_H__
#define __BMP085_H__

#include <linux/ioctl.h>  

#define BMP085_NAME "bmp085"

#define BMP085_IOCTL_BASE 78

#define BMP085_IOCTL_SET_DELAY _IOW(BMP085_IOCTL_BASE, 0, int)
#define BMP085_IOCTL_GET_DELAY _IOR(BMP085_IOCTL_BASE, 1, int)
#define BMP085_IOCTL_SET_ENABLE _IOW(BMP085_IOCTL_BASE, 2, int)
#define BMP085_IOCTL_GET_ENABLE _IOR(BMP085_IOCTL_BASE, 3, int)
#define BMP085_IOCTL_ACCURACY _IOW(BMP085_IOCTL_BASE, 4, int)

#endif


