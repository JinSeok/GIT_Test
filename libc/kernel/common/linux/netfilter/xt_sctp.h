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
#ifndef _XT_SCTP_H_
#define _XT_SCTP_H_

#define XT_SCTP_SRC_PORTS 0x01
#define XT_SCTP_DEST_PORTS 0x02
#define XT_SCTP_CHUNK_TYPES 0x04

#define XT_SCTP_VALID_FLAGS 0x07

#define ELEMCOUNT(x) (sizeof(x)/sizeof(x[0]))

struct xt_sctp_flag_info {
 u_int8_t chunktype;
 u_int8_t flag;
 u_int8_t flag_mask;
};

#define XT_NUM_SCTP_FLAGS 4

struct xt_sctp_info {
 u_int16_t dpts[2];
 u_int16_t spts[2];

 u_int32_t chunkmap[256 / sizeof (u_int32_t)];

#define SCTP_CHUNK_MATCH_ANY 0x01  
#define SCTP_CHUNK_MATCH_ALL 0x02  
#define SCTP_CHUNK_MATCH_ONLY 0x04  

 u_int32_t chunk_match_type;
 struct xt_sctp_flag_info flag_info[XT_NUM_SCTP_FLAGS];
 int flag_count;

 u_int32_t flags;
 u_int32_t invflags;
};

#define bytes(type) (sizeof(type) * 8)

#define SCTP_CHUNKMAP_SET(chunkmap, type)   do {   chunkmap[type / bytes(u_int32_t)] |=   1 << (type % bytes(u_int32_t));   } while (0)

#define SCTP_CHUNKMAP_CLEAR(chunkmap, type)   do {   chunkmap[type / bytes(u_int32_t)] &=   ~(1 << (type % bytes(u_int32_t)));   } while (0)

#define SCTP_CHUNKMAP_IS_SET(chunkmap, type)  ({   (chunkmap[type / bytes (u_int32_t)] &   (1 << (type % bytes (u_int32_t)))) ? 1: 0;  })

#define SCTP_CHUNKMAP_RESET(chunkmap)   do {   int i;   for (i = 0; i < ELEMCOUNT(chunkmap); i++)   chunkmap[i] = 0;   } while (0)

#define SCTP_CHUNKMAP_SET_ALL(chunkmap)   do {   int i;   for (i = 0; i < ELEMCOUNT(chunkmap); i++)   chunkmap[i] = ~0;   } while (0)

#define SCTP_CHUNKMAP_COPY(destmap, srcmap)   do {   int i;   for (i = 0; i < ELEMCOUNT(chunkmap); i++)   destmap[i] = srcmap[i];   } while (0)

#define SCTP_CHUNKMAP_IS_CLEAR(chunkmap)  ({   int i;   int flag = 1;   for (i = 0; i < ELEMCOUNT(chunkmap); i++) {   if (chunkmap[i]) {   flag = 0;   break;   }   }   flag;  })

#define SCTP_CHUNKMAP_IS_ALL_SET(chunkmap)  ({   int i;   int flag = 1;   for (i = 0; i < ELEMCOUNT(chunkmap); i++) {   if (chunkmap[i] != ~0) {   flag = 0;   break;   }   }   flag;  })

#endif

