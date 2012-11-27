DESCRIPTION = "Linux kernel for vuplus"
SECTION = "kernel"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=d7810fab7487fb0aad327b76f1be7cd7"

KV = "3.1.1"
SRCREV = "r2"

MODULE = "linux-3.1.1"

SRC_URI += "http://archive.vuplus.com/download/kernel/linux-${KV}_${SRCREV}.tar.bz2 \
	file://fix_cpu_proc.patch;patch=1;pnum=1 \
        file://${MACHINE}_defconfig \
	file://igmp.patch;patch=1;pnum=1 \
"

SRC_URI[md5sum] = "4dc3ac322453abbfaade7020cddea205"
SRC_URI[sha256sum] = "1d18eb39677a23eace6b27ee25656c25f21b57be7e77a2adcdd15c76d1c3e875"

S = "${WORKDIR}/linux-${KV}"

inherit kernel

FILES_kernel-vmlinux = "/boot/vmlinux-3.1.1"
FILES_kernel-image = "/boot/vmlinux.gz"

export OS = "Linux"
KERNEL_IMAGETYPE = "vmlinux"
KERNEL_OUTPUT = "vmlinux"
KERNEL_OBJECT_SUFFIX = "ko"


do_configure_prepend() {
        oe_machinstall -m 0644 ${WORKDIR}/${MACHINE}_defconfig ${S}/.config
        oe_runmake oldconfig
}

kernel_do_install_append() {
        install -d ${D}/${KERNEL_IMAGEDEST}
        install -m 0755 ${KERNEL_OUTPUT} ${D}/${KERNEL_IMAGEDEST}
        gzip ${D}/${KERNEL_IMAGEDEST}/${KERNEL_IMAGETYPE}
}

pkg_postinst_kernel-image () {
        if [ -d /proc/stb ] ; then
                flash_eraseall -j /dev/mtd1
                nandwrite -p /dev/mtd1 /${KERNEL_IMAGEDEST}/${KERNEL_IMAGETYPE}.gz
        fi
        rm -f /${KERNEL_IMAGEDEST}/${KERNEL_IMAGETYPE}.gz
        true
}
