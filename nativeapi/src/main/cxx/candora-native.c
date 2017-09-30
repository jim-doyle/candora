#include <stdio.h>
#include <string.h>
#include <errno.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/ioctl.h>
#include <net/if.h>
#include <linux/can.h>
#include <linux/can/raw.h>
#include <linux/sockios.h>

// https://www3.ntu.edu.sg/home/ehchua/programming/java/JavaNativeInterface.html


// Proof of concept!
int main(int argc, char ** argv) {

  const int recv_to_seconds = 0;
  const int recv_to_microseconds = 0;

  printf("starting.");
  int s;

  s = socket(PF_CAN, SOCK_RAW, CAN_RAW);
  if (s < 0) {
    exit(1);
  }
  fprintf(stdout, "startup: socket opened.\n");
  fflush(stdout);

  // Interface bind
  struct ifreq ifr;
  strcpy(ifr.ifr_name, "can0" );
  if (ioctl(s, SIOCGIFINDEX, &ifr) < 0) {
    exit(1);
  }

  const int enable = 1;
  setsockopt(s, SOL_CAN_RAW, CAN_RAW_FD_FRAMES,
               &enable, sizeof(enable));
  printf("startup: set socket opt\n");

  // set receive timeout, if needed
  if (recv_to_seconds > 0 || recv_to_microseconds > 0) {
    struct timeval recv_timeout;
    recv_timeout.tv_sec=recv_to_seconds;
    recv_timeout.tv_usec = recv_to_microseconds;   // just under 1 second
;
    if (setsockopt(s, SOL_SOCKET, SO_RCVTIMEO, (const char *)&recv_timeout,
                 sizeof(struct timeval)) <0 ) {
      int e = errno;
      printf("receive timeout failed. %d %s \n", e, strerror(e));
      exit(1);
    }
  } else {
    // warning we will be using blocking reads.
  }

  struct sockaddr_can addr;
  addr.can_family = AF_CAN;
  addr.can_ifindex = ifr.ifr_ifindex;
  if (bind(s, (struct sockaddr *)&addr, sizeof(addr)) < 0) {
    exit(1);
  }
  printf("startup: socket bound\n");


  // Lets try to transmit something.

  struct canfd_frame xmitframe;
  xmitframe.can_id = 0x1234;
  xmitframe.len = 2;
  xmitframe.flags = 0;
  xmitframe.data[0] = 0x14;
  xmitframe.data[1] = 0x88;

  int nbytes = sendto(s, &xmitframe, sizeof(struct can_frame),
                      0, (struct sockaddr*)&addr, sizeof(addr));
  if (nbytes < 0) {
    int e = errno;
    printf("sendto failed. %d %s \n", e, strerror(e));
  } else {
    printf("sent frame out!\n");
  }
  fflush(stdout);




  struct canfd_frame recvframe;
  socklen_t len = sizeof(addr);
  printf("Waiting for receive.\n");
  nbytes = recvfrom(s, &recvframe, sizeof(struct canfd_frame),
                    0, (struct sockaddr*)&addr, &len);
  if (nbytes < 0) {
    int e = errno;
    if (e == EAGAIN) {
      // recv timeout.....
    }

    printf("sendto failed. %d %s \n", e, strerror(e));
    // errno 11 - means nothing was received.
  } else {
    struct timeval recv_timestamp;
    ioctl(s, SIOCGSTAMP, &recv_timestamp);
  }
  close(s);

  return nbytes;
}
