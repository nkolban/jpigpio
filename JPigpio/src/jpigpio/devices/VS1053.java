package jpigpio.devices;

import java.io.File;
import java.io.IOException;

import jpigpio.FileIO;
import jpigpio.JPigpio;
import jpigpio.PigpioException;
import jpigpio.Utils;
import jpigpio.WrongModeException;
import jpigpio.impl.SPI;

/**
 * The VS1053 is an audio device accessed via SPI. We will assume that the VS1053 is connected to hardware SPI as normal. Since the pin out of the VS1053 development board is:
 * +------+------+------+------+-----+ | XCS | DREQ | MOSI | DGND | 5V | +------+------+------+------+-----+ | XDCS | XRST | SCK | MISO | 5V | +------+------+------+------+-----+
 * 
 * We will assume:
 * 
 * XCS - SPI_CE0 XDCS - SPI_CE1 DREQ - ?? XRST - 5V MOSI - SPI_MOSI SCK - SPI_SCLK DGND - GND MISO - SPI_MISO 5V - 5V
 * 
 * @author kolban
 *
 */
public class VS1053 {
	/* SCI registers */

	// SCI Commands
	private final boolean DEVICE_ENABLE = false;
	private final boolean DEVICE_DISABLE = true;
	private final int READ = 0b11;
	private final int WRITE = 0b10;

	private final int SCI_MODE = 0x00;
	private final int SCI_STATUS = 0x01;
	private final int SCI_BASS = 0x02;
	private final int SCI_CLOCKF = 0x03;
	private final int SCI_DECODE_TIME = 0x04;
	private final int SCI_AUDATA = 0x05;
	private final int SCI_WRAM = 0x06;
	private final int SCI_WRAMADDR = 0x07;
	private final int SCI_HDAT0 = 0x08;
	private final int SCI_HDAT1 = 0x09;
	private final int SCI_AIADDR = 0x0A;
	private final int SCI_VOL = 0x0B;
	private final int SCI_AICTRL0 = 0x0C;
	private final int SCI_AICTRL1 = 0x0D;
	private final int SCI_AICTRL2 = 0x0E;
	private final int SCI_AICTRL3 = 0x0F;

	/* SCI register recording aliases */

	private final int SCI_RECRATE = 0x0C; /* (AICTRL0) VS1063, VS1053 */
	private final int SCI_RECGAIN = 0x0D; /* (AICTRL1) VS1063, VS1053, VS1033, VS1003 */
	private final int SCI_RECMAXAUTO = 0x0E; /* (AICTRL2) VS1063, VS1053, VS1033 */
	private final int SCI_RECMODE = 0x0F; /* (AICTRL3) VS1063, VS1053 */

	/* SCI_MODE bits */

	private final int SM_DIFF_B = 0;
	private final int SM_LAYER12_B = 1;
	private final int SM_RESET_B = 2;
	private final int SM_CANCEL_B = 3;
	private final int SM_EARSPEAKER_LO_B = 4;
	private final int SM_TESTS_B = 5;
	private final int SM_STREAM_B = 6;
	private final int SM_EARSPEAKER_HI_B = 7;
	private final int SM_DACT_B = 8;
	private final int SM_SDIORD_B = 9;
	private final int SM_SDISHARE_B = 10;
	private final int SM_SDINEW_B = 11;
	private final int SM_ADPCM_B = 12;
	private final int SM_LINE1_B = 14;
	private final int SM_CLK_RANGE_B = 15;
	private final int SM_DIFF = (1 << SM_DIFF_B);
	private final int SM_LAYER12 = (1 << SM_LAYER12_B);
	private final int SM_RESET = (1 << SM_RESET_B);
	private final int SM_CANCEL = (1 << SM_CANCEL_B);
	private final int SM_EARSPEAKER_LO = (1 << SM_EARSPEAKER_LO_B);
	private final int SM_TESTS = (1 << SM_TESTS_B);
	private final int SM_STREAM = (1 << SM_STREAM_B);
	private final int SM_EARSPEAKER_HI = (1 << SM_EARSPEAKER_HI_B);
	private final int SM_DACT = (1 << SM_DACT_B);
	private final int SM_SDIORD = (1 << SM_SDIORD_B);
	private final int SM_SDISHARE = (1 << SM_SDISHARE_B);
	private final int SM_SDINEW = (1 << SM_SDINEW_B);
	private final int SM_ADPCM = (1 << SM_ADPCM_B);
	private final int SM_LINE1 = (1 << SM_LINE1_B);
	private final int SM_CLK_RANGE = (1 << SM_CLK_RANGE_B);

	private final int SM_ICONF_BITS = 2;
	private final int SM_ICONF_MASK = 0x00c0;
	private final int SM_EARSPEAKER_1103_BITS = 2;
	private final int SM_EARSPEAKER_1103_MASK = 0x3000;

	/* SCI_STATUS bits */

	private final int SS_REFERENCE_SEL_B = 0;
	private final int SS_AD_CLOCK_B = 1;
	private final int SS_APDOWN1_B = 2;
	private final int SS_APDOWN2_B = 3;
	private final int SS_VER_B = 4;
	private final int SS_VCM_DISABLE_B = 10;
	private final int SS_VCM_OVERLOAD_B = 11;
	private final int SS_SWING_B = 12;
	private final int SS_DO_NOT_JUMP_B = 15;

	private final int SS_REFERENCE_SEL = (1 << 0); /* VS1063, VS1053 */
	private final int SS_AVOL = (1 << 0); /* VS1033, VS1003, VS1103, VS1011 */
	private final int SS_AD_CLOCK = (1 << 1); /* VS1063, VS1053 */
	private final int SS_APDOWN1 = (1 << 2);
	private final int SS_APDOWN2 = (1 << 3);
	private final int SS_VER = (1 << 4);
	private final int SS_VCM_DISABLE = (1 << 10); /* VS1063, VS1053 */
	private final int SS_VCM_OVERLOAD = (1 << 11); /* VS1063, VS1053 */
	private final int SS_SWING = (1 << 12); /* VS1063, VS1053 */
	private final int SS_DO_NOT_JUMP = (1 << 15); /* VS1063, VS1053 */

	private final int SS_SWING_BITS = 3;
	private final int SS_SWING_MASK = 0x7000;
	private final int SS_VER_BITS = 4;
	private final int SS_VER_MASK = 0x00f0;
	private final int SS_AVOL_BITS = 2;
	private final int SS_AVOL_MASK = 0x0003;

	private final int SS_VER_VS1001 = 0x00;
	private final int SS_VER_VS1011 = 0x10;
	private final int SS_VER_VS1002 = 0x20;
	private final int SS_VER_VS1003 = 0x30;
	private final int SS_VER_VS1053 = 0x40;
	private final int SS_VER_VS8053 = 0x40;
	private final int SS_VER_VS1033 = 0x50;
	private final int SS_VER_VS1063 = 0x60;
	private final int SS_VER_VS1103 = 0x70;

	// /* SCI_BASS bits */
	//
	private final int ST_AMPLITUDE_B = 12;
	private final int ST_FREQLIMIT_B = 8;
	private final int SB_AMPLITUDE_B = 4;
	private final int SB_FREQLIMIT_B = 0;

	private final int ST_AMPLITUDE = (1 << 12);
	private final int ST_FREQLIMIT = (1 << 8);
	private final int SB_AMPLITUDE = (1 << 4);
	private final int SB_FREQLIMIT = (1 << 0);

	private final int ST_AMPLITUDE_BITS = 4;
	private final int ST_AMPLITUDE_MASK = 0xf000;
	private final int ST_FREQLIMIT_BITS = 4;
	private final int ST_FREQLIMIT_MASK = 0x0f00;
	private final int SB_AMPLITUDE_BITS = 4;
	private final int SB_AMPLITUDE_MASK = 0x00f0;
	private final int SB_FREQLIMIT_BITS = 4;
	private final int SB_FREQLIMIT_MASK = 0x000f;
	//
	//
	// /* SCI_CLOCKF bits */
	//
	private final int SC_MULT_B = 13; /* VS1063, VS1053, VS1033, VS1103, VS1003 */
	private final int SC_ADD_B = 11; /* VS1063, VS1053, VS1033, VS1003 */
	private final int SC_FREQ_B = 0; /* VS1063, VS1053, VS1033, VS1103, VS1003 */

	private final int SC_MULT = (1 << 13); /* VS1063, VS1053, VS1033, VS1103, VS1003 */
	private final int SC_ADD = (1 << 11); /* VS1063, VS1053, VS1033, VS1003 */
	private final int SC_FREQ = (1 << 0); /* VS1063, VS1053, VS1033, VS1103, VS1003 */

	private final int SC_MULT_BITS = 3;
	private final int SC_MULT_MASK = 0xe000;
	private final int SC_ADD_BITS = 2;
	private final int SC_ADD_MASK = 0x1800;
	private final int SC_FREQ_BITS = 11;
	private final int SC_FREQ_MASK = 0x07ff;
	//
	// /* The following macro is for VS1063, VS1053, VS1033, VS1003, VS1103.
	// Divide hz by two when calling if SM_CLK_RANGE = 1 */
	// #define HZ_TO_SC_FREQ(hz) (((hz)-8000000+2000)/4000)
	//

	//
	//
	// /* Following are for VS1053 and VS1063 */
	// private final int SC_MULT_53_10X= 0x0000;
	// private final int SC_MULT_53_20X =0x2000;
	// private final int SC_MULT_53_25X =0x4000;
	// private final int SC_MULT_53_30X =0x6000;
	// private final int SC_MULT_53_35X =0x8000;
	// private final int SC_MULT_53_40X =0xa000;
	// private final int SC_MULT_53_45X =0xc000;
	// private final int SC_MULT_53_50X =0xe000;
	//
	// /* Following are for VS1003 and VS1033 */
	// #define SC_ADD_03_00X 0x0000
	// #define SC_ADD_03_05X 0x0800
	// #define SC_ADD_03_10X 0x1000
	// #define SC_ADD_03_15X 0x1800
	//
	// /* Following are for VS1053 and VS1063 */
	// #define SC_ADD_53_00X 0x0000
	// #define SC_ADD_53_10X 0x0800
	// #define SC_ADD_53_15X 0x1000
	// #define SC_ADD_53_20X 0x1800
	//
	//
	// /* SCI_WRAMADDR bits */
	//
	// #define SCI_WRAM_X_START 0x0000
	// #define SCI_WRAM_Y_START 0x4000
	// #define SCI_WRAM_I_START 0x8000
	// #define SCI_WRAM_IO_START 0xC000
	// #define SCI_WRAM_PARAMETRIC_START 0xC0C0 /* VS1063 */
	// #define SCI_WRAM_Y2_START 0xE000 /* VS1063 */
	//
	// #define SCI_WRAM_X_OFFSET 0x0000
	// #define SCI_WRAM_Y_OFFSET 0x4000
	// #define SCI_WRAM_I_OFFSET 0x8000
	// #define SCI_WRAM_IO_OFFSET 0x0000 /* I/O addresses are @0xC000 -> no offset */
	//
	//
	// /* SCI_VOL bits */
	//
	// #define SV_LEFT_B 8
	// #define SV_RIGHT_B 0
	//
	// #define SV_LEFT (1<<8)
	// #define SV_RIGHT (1<<0)
	//
	// #define SV_LEFT_BITS 8
	// #define SV_LEFT_MASK 0xFF00
	// #define SV_RIGHT_BITS 8
	// #define SV_RIGHT_MASK 0x00FF
	//
	//
	//
	// /* SCI_RECMODE bits for VS1053 */
	//
	// #define RM_53_FORMAT_B 2
	// #define RM_53_ADC_MODE_B 0
	//
	// #define RM_53_FORMAT (1<< 2)
	// #define RM_53_ADC_MODE (1<< 0)
	//
	// #define RM_53_ADCMODE_BITS 2
	// #define RM_53_ADCMODE_MASK 0x0003
	//
	// #define RM_53_FORMAT_IMA_ADPCM 0x0000
	// #define RM_53_FORMAT_PCM 0x0004
	//
	// #define RM_53_ADC_MODE_JOINT_AGC_STEREO 0x0000
	// #define RM_53_ADC_MODE_DUAL_AGC_STEREO 0x0001
	// #define RM_53_ADC_MODE_LEFT 0x0002
	// #define RM_53_ADC_MODE_RIGHT 0x0003
	//
	//
	// /* VS1063 definitions */
	//
	// /* VS1063 / VS1053 Parametric */
	// #define PAR_CHIP_ID 0x1e00 /* VS1063, VS1053, 32 bits */
	// #define PAR_VERSION 0x1e02 /* VS1063, VS1053 */
	// #define PAR_CONFIG1 0x1e03 /* VS1063, VS1053 */
	// #define PAR_PLAY_SPEED 0x1e04 /* VS1063, VS1053 */
	// #define PAR_BITRATE_PER_100 0x1e05 /* VS1063 */
	// #define PAR_BYTERATE 0x1e05 /* VS1053 */
	// #define PAR_END_FILL_BYTE 0x1e06 /* VS1063, VS1053 */
	// #define PAR_RATE_TUNE 0x1e07 /* VS1063, 32 bits */
	// #define PAR_PLAY_MODE 0x1e09 /* VS1063 */
	// #define PAR_SAMPLE_COUNTER 0x1e0a /* VS1063, 32 bits */
	// #define PAR_VU_METER 0x1e0c /* VS1063 */
	// #define PAR_AD_MIXER_GAIN 0x1e0d /* VS1063 */
	// #define PAR_AD_MIXER_CONFIG 0x1e0e /* VS1063 */
	// #define PAR_PCM_MIXER_RATE 0x1e0f /* VS1063 */
	// #define PAR_PCM_MIXER_FREE 0x1e10 /* VS1063 */
	// #define PAR_PCM_MIXER_VOL 0x1e11 /* VS1063 */
	// #define PAR_EQ5_DUMMY 0x1e12 /* VS1063 */
	// #define PAR_EQ5_LEVEL1 0x1e13 /* VS1063 */
	// #define PAR_EQ5_FREQ1 0x1e14 /* VS1063 */
	// #define PAR_EQ5_LEVEL2 0x1e15 /* VS1063 */
	// #define PAR_EQ5_FREQ2 0x1e16 /* VS1063 */
	// #define PAR_JUMP_POINTS 0x1e16 /* VS1053 */
	// #define PAR_EQ5_LEVEL3 0x1e17 /* VS1063 */
	// #define PAR_EQ5_FREQ3 0x1e18 /* VS1063 */
	// #define PAR_EQ5_LEVEL4 0x1e19 /* VS1063 */
	// #define PAR_EQ5_FREQ4 0x1e1a /* VS1063 */
	// #define PAR_EQ5_LEVEL5 0x1e1b /* VS1063 */
	// #define PAR_EQ5_UPDATED 0x1e1c /* VS1063 */
	// #define PAR_SPEED_SHIFTER 0x1e1d /* VS1063 */
	// #define PAR_EARSPEAKER_LEVEL 0x1e1e /* VS1063 */
	// #define PAR_SDI_FREE 0x1e1f /* VS1063 */
	// #define PAR_AUDIO_FILL 0x1e20 /* VS1063 */
	// #define PAR_RESERVED0 0x1e21 /* VS1063 */
	// #define PAR_RESERVED1 0x1e22 /* VS1063 */
	// #define PAR_RESERVED2 0x1e23 /* VS1063 */
	// #define PAR_RESERVED3 0x1e24 /* VS1063 */
	// #define PAR_LATEST_SOF 0x1e25 /* VS1063, 32 bits */
	// #define PAR_LATEST_JUMP 0x1e26 /* VS1053 */
	// #define PAR_POSITION_MSEC 0x1e27 /* VS1063, VS1053, 32 bits */
	// #define PAR_RESYNC 0x1e29 /* VS1063, VS1053 */
	//
	// /* The following addresses are shared between modes. */
	// /* Generic pointer */
	// #define PAR_GENERIC 0x1e2a /* VS1063, VS1053 */
	//
	// /* Encoder mode */
	// #define PAR_ENC_TX_UART_DIV 0x1e2a /* VS1063 */
	// #define PAR_ENC_TX_UART_BYTE_SPEED 0x1e2b /* VS1063 */
	// #define PAR_ENC_TX_PAUSE_GPIO 0x1e2c /* VS1063 */
	// #define PAR_ENC_AEC_ADAPT_MULTIPLIER 0x1e2d /* VS1063 */
	// #define PAR_ENC_RESERVED 0x1e2e /* VS1063 */
	// #define PAR_ENC_CHANNEL_MAX 0x1e3c /* VS1063 */
	// #define PAR_ENC_SERIAL_NUMBER 0x1e3e /* VS1063 */
	//
	// /* Decoding WMA */
	// #define PAR_WMA_CUR_PACKET_SIZE 0x1e2a /* VS1063, VS1053, 32 bits */
	// #define PAR_WMA_PACKET_SIZE 0x1e2c /* VS1063, VS1053, 32 bits */
	//
	// /* Decoding AAC */
	// #define PAR_AAC_SCE_FOUND_MASK 0x1e2a /* VS1063, VS1053 */
	// #define PAR_AAC_CPE_FOUND_MASK 0x1e2b /* VS1063, VS1053 */
	// #define PAR_AAC_LFE_FOUND_MASK 0x1e2c /* VS1063, VS1053 */
	// #define PAR_AAC_PLAY_SELECT 0x1e2d /* VS1063, VS1053 */
	// #define PAR_AAC_DYN_COMPRESS 0x1e2e /* VS1063, VS1053 */
	// #define PAR_AAC_DYN_BOOST 0x1e2f /* VS1063, VS1053 */
	// #define PAR_AAC_SBR_AND_PS_STATUS 0x1e30 /* VS1063, VS1053 */
	// #define PAR_AAC_SBR_PS_FLAGS 0x1e31 /* VS1063 */
	//
	//
	// /* Decoding MIDI (VS1053) */
	// #define PAR_MIDI_BYTES_LEFT 0x1e2a /* VS1053, 32 bits */
	//
	// /* Decoding Vorbis */
	// #define PAR_VORBIS_GAIN 0x1e2a 0x1e30 /* VS1063, VS1053 */
	//
	//
	// /* Bit definitions for parametric registers with bitfields */
	// #define PAR_CONFIG1_DIS_WMA_B 15 /* VS1063 */
	// #define PAR_CONFIG1_DIS_AAC_B 14 /* VS1063 */
	// #define PAR_CONFIG1_DIS_MP3_B 13 /* VS1063 */
	// #define PAR_CONFIG1_DIS_FLAC_B 12 /* VS1063 */
	// #define PAR_CONFIG1_DIS_CRC_B 8 /* VS1063 */
	// #define PAR_CONFIG1_AAC_PS_B 6 /* VS1063, VS1053 */
	// #define PAR_CONFIG1_AAC_SBR_B 4 /* VS1063, VS1053 */
	// #define PAR_CONFIG1_MIDI_REVERB_B 0 /* VS1053 */
	//
	// #define PAR_CONFIG1_DIS_WMA (1<<15) /* VS1063 */
	// #define PAR_CONFIG1_DIS_AAC (1<<14) /* VS1063 */
	// #define PAR_CONFIG1_DIS_MP3 (1<<13) /* VS1063 */
	// #define PAR_CONFIG1_DIS_FLAC (1<<12) /* VS1063 */
	// #define PAR_CONFIG1_DIS_CRC (1<< 8) /* VS1063 */
	// #define PAR_CONFIG1_AAC_PS (1<< 6) /* VS1063, VS1053 */
	// #define PAR_CONFIG1_AAC_SBR (1<< 4) /* VS1063, VS1053 */
	// #define PAR_CONFIG1_MIDI_REVERB (1<< 0) /* VS1053 */
	//
	// #define PAR_CONFIG1_AAC_PS_BITS 2 /* VS1063, VS1053 */
	// #define PAR_CONFIG1_AAC_PS_MASK 0x00c0 /* VS1063, VS1053 */
	// #define PAR_CONFIG1_AAC_SBR_BITS 2 /* VS1063, VS1053 */
	// #define PAR_CONFIG1_AAC_SBR_MASK 0x0030 /* VS1063, VS1053 */
	//
	// #define PAR_CONFIG1_AAC_SBR_ALWAYS_UPSAMPLE 0x0000 /* VS1063, VS1053 */
	// #define PAR_CONFIG1_AAC_SBR_SELECTIVE_UPSAMPLE 0x0010 /* VS1063, VS1053 */
	// #define PAR_CONFIG1_AAC_SBR_NEVER_UPSAMPLE 0x0020 /* VS1063, VS1053 */
	// #define PAR_CONFIG1_AAC_SBR_DISABLE 0x0030 /* VS1063, VS1053 */
	//
	// #define PAR_CONFIG1_AAC_PS_NORMAL 0x0000 /* VS1063, VS1053 */
	// #define PAR_CONFIG1_AAC_PS_DOWNSAMPLED 0x0040 /* VS1063, VS1053 */
	// #define PAR_CONFIG1_AAC_PS_DISABLE 0x00c0 /* VS1063, VS1053 */
	//
	// #define PAR_PLAY_MODE_SPEED_SHIFTER_ENA_B 6 /* VS1063 */
	// #define PAR_PLAY_MODE_EQ5_ENA_B 5 /* VS1063 */
	// #define PAR_PLAY_MODE_PCM_MIXER_ENA_B 4 /* VS1063 */
	// #define PAR_PLAY_MODE_AD_MIXER_ENA_B 3 /* VS1063 */
	// #define PAR_PLAY_MODE_VU_METER_ENA_B 2 /* VS1063 */
	// #define PAR_PLAY_MODE_PAUSE_ENA_B 1 /* VS1063 */
	// #define PAR_PLAY_MODE_MONO_ENA_B 0 /* VS1063 */
	//
	// #define PAR_PLAY_MODE_SPEED_SHIFTER_ENA (1<<6) /* VS1063 */
	// #define PAR_PLAY_MODE_EQ5_ENA (1<<5) /* VS1063 */
	// #define PAR_PLAY_MODE_PCM_MIXER_ENA (1<<4) /* VS1063 */
	// #define PAR_PLAY_MODE_AD_MIXER_ENA (1<<3) /* VS1063 */
	// #define PAR_PLAY_MODE_VU_METER_ENA (1<<2) /* VS1063 */
	// #define PAR_PLAY_MODE_PAUSE_ENA (1<<1) /* VS1063 */
	// #define PAR_PLAY_MODE_MONO_ENA (1<<0) /* VS1063 */
	//
	// #define PAR_VU_METER_LEFT_BITS 8 /* VS1063 */
	// #define PAR_VU_METER_LEFT_MASK 0xFF00 /* VS1063 */
	// #define PAR_VU_METER_RIGHT_BITS 8 /* VS1063 */
	// #define PAR_VU_METER_RIGHT_MASK 0x00FF /* VS1063 */
	//
	// #define PAR_AD_MIXER_CONFIG_MODE_B 2 /* VS1063 */
	// #define PAR_AD_MIXER_CONFIG_RATE_B 2 /* VS1063 */
	//
	// #define PAR_AD_MIXER_CONFIG_MODE_BITS 2 /* VS1063 */
	// #define PAR_AD_MIXER_CONFIG_MODE_MASK 0x000c /* VS1063 */
	// #define PAR_AD_MIXER_CONFIG_RATE_BITS 2 /* VS1063 */
	// #define PAR_AD_MIXER_CONFIG_RATE_MASK 0x0003 /* VS1063 */
	//
	// #define PAR_AD_MIXER_CONFIG_RATE_192K 0x0000 /* VS1063 */
	// #define PAR_AD_MIXER_CONFIG_RATE_96K 0x0001 /* VS1063 */
	// #define PAR_AD_MIXER_CONFIG_RATE_48K 0x0002 /* VS1063 */
	// #define PAR_AD_MIXER_CONFIG_RATE_24K 0x0003 /* VS1063 */
	//
	// #define PAR_AD_MIXER_CONFIG_MODE_STEREO 0x0000 /* VS1063 */
	// #define PAR_AD_MIXER_CONFIG_MODE_MONO 0x0040 /* VS1063 */
	// #define PAR_AD_MIXER_CONFIG_MODE_LEFT 0x0080 /* VS1063 */
	// #define PAR_AD_MIXER_CONFIG_MODE_RIGHT 0x00c0 /* VS1063 */
	//
	// #define PAR_AAC_SBR_AND_PS_STATUS_SBR_PRESENT_B 0 /* VS1063, VS1053 */
	// #define PAR_AAC_SBR_AND_PS_STATUS_UPSAMPLING_ACTIVE_B 1 /* VS1063, VS1053 */
	// #define PAR_AAC_SBR_AND_PS_STATUS_PS_PRESENT_B 2 /* VS1063, VS1053 */
	// #define PAR_AAC_SBR_AND_PS_STATUS_PS_ACTIVE_B 3 /* VS1063, VS1053 */
	//
	// #define PAR_AAC_SBR_AND_PS_STATUS_SBR_PRESENT (1<<0) /* VS1063, VS1053 */
	// #define PAR_AAC_SBR_AND_PS_STATUS_UPSAMPLING_ACTIVE (1<<1) /* VS1063, VS1053 */
	// #define PAR_AAC_SBR_AND_PS_STATUS_PS_PRESENT (1<<2) /* VS1063, VS1053 */
	// #define PAR_AAC_SBR_AND_PS_STATUS_PS_ACTIVE (1<<3) /* VS1063, VS1053 */
	//
	//
	// #endif /* !VS10XX_MICROCONTROLLER_DEFINITIONS_H */

	private JPigpio pigpio;
	private SPI sciSpi;
	private SPI sdiSpi;
	private int gpioDREQ;

	private class Parameters {
		private int version;
		private int config1;
		private int playSpeed;
		private int byteRate;
		private int endFillByte;

		public void retrieve() throws PigpioException {
			version = readRamFromAddress(0x1e02);
			config1 = readRamFromAddress(0x1e03);
			playSpeed = readRamFromAddress(0x1e04);
			byteRate = readRamFromAddress(0x1e05);
			endFillByte = readRamFromAddress(0x1e06);
		}

		@Override
		public String toString() {
			String ret = "";
			ret += String.format("version: %d", version);
			ret += String.format(", config1: %x", config1);
			ret += String.format(", playSpeed: %d", playSpeed);
			ret += String.format(", byteRate: %d", byteRate);
			ret += String.format(", endFillByte: %d", endFillByte);
			return ret;
		}
	};

	public VS1053(JPigpio pigpio, int gpioDREQ) throws PigpioException {
		this.pigpio = pigpio;
		this.gpioDREQ = gpioDREQ;
		if (pigpio.gpioGetMode(gpioDREQ) != JPigpio.PI_INPUT) {
			throw new WrongModeException(gpioDREQ);
		}
		this.sciSpi = new SPI(pigpio, JPigpio.PI_SPI_CHANNEL0, JPigpio.PI_SPI_BAUD_2MHZ, 0);
		this.sdiSpi = new SPI(pigpio, JPigpio.PI_SPI_CHANNEL1, JPigpio.PI_SPI_BAUD_2MHZ, 0);
	}

	/**
	 * Read a value from a device register.
	 * @param register The register to be read.
	 * @return The value read from the device register (16 bits).
	 * @throws PigpioException
	 */
	private int readSci(int register) throws PigpioException {
		waitForReady();
		byte txData[] = { READ, (byte) register, 0, 0 };
		byte rxData[] = new byte[4];
		sciSpi.xfer(txData, rxData);
		byte word[] = new byte[2];
		word[0] = rxData[2];
		word[1] = rxData[3];
		return Utils.byteWordToInt(word);
	} // End of readSci

	/**
	 * Write data to a device register
	 * @param register The register to be written. 
	 * @param value The value to be written to the register (16 bits).
	 * @throws PigpioException
	 */
	private void writeSci(int register, int value) throws PigpioException {
		waitForReady();
		byte data[] = { WRITE, (byte) register, (byte) ((value >>> 8) & 0xff), (byte) (value & 0xff) };
		sciSpi.write(data);
	} // End of writeSci

	/**
	 * Write data to the SDI SPI data bus.
	 * @param data The data to write to the SDI SPI data bus.
	 * @throws PigpioException
	 */
	private void writeSdi(byte data[]) throws PigpioException {
		waitForReady();
		sdiSpi.write(data);
	} // End of writeSdi

	/**
	 * Retrieve the device mode register value.
	 * @return The value of the device mode register (16 bits).
	 * @throws PigpioException
	 */
	public int getMode() throws PigpioException {
		return readSci(SCI_MODE);
	} // End of getMode
	
	/**
	 * Set the device mode register to a supplied value.
	 * @param value The value to be placed in the mode register (16 bits).
	 * @throws PigpioException
	 */
	public void setMode(int value) throws PigpioException {
		writeSci(SCI_MODE, value);
	} // End of setMode

	/**
	 * Retrieve the status register of the device.
	 * @return The status register of the device (16 bits).
	 * @throws PigpioException
	 */
	public int getStatus() throws PigpioException {
		return readSci(SCI_STATUS);
	} // End of getStatus

	public int getAudata() throws PigpioException {
		return readSci(SCI_AUDATA);
	} // End of getAudata
	
	public void setAudata(int value) throws PigpioException {
		writeSci(SCI_AUDATA, value);
	} // End of setAudata

	/**
	 * Read the volume register of the device.
	 * @return The volume register of the device (16 bits).
	 * @throws PigpioException
	 */
	public int getVolume() throws PigpioException {
		return readSci(SCI_VOL);
	} // End of getVolume
	
	/**
	 * A volume value is replicated in the high and low bytes of the volume word
	 * @param value The byte value of the volume
	 * @throws PigpioException
	 */
	public void setVolume(int value) throws PigpioException {
		int newValue = (value & 0xff) << 8 | (value & 0xff);
		writeSci(SCI_VOL, newValue);
	} // End of setVolume
	

	/**
	 * Read the ClockF register of the device.
	 * @return The ClockF register of the device (16 bits).
	 * @throws PigpioException
	 */
	public int getClockF() throws PigpioException {
		return readSci(SCI_CLOCKF);
	} // End of getClockF
	
	/**
	 * Set the ClockF value.
	 * @param value The value of the ClockF register to be set.
	 * @throws PigpioException
	 */
	public void setClockF(int value) throws PigpioException {
		writeSci(SCI_CLOCKF, value);
	} // End of setClockF

	/**
	 * Get the Bass register value.
	 * @return The value of the Bass register.
	 * @throws PigpioException
	 */
	public int getBass() throws PigpioException {
		waitForReady();
		return readSci(SCI_BASS);
	} // End of getBass

	/**
	 * Set the Line vs Mic mode.  A value of true sets Line mode while a value of
	 * false sets Mic mode.
	 * @param value The mode to use.  True means use line input while false means use
	 * Mic input.
	 * @throws PigpioException
	 */
	public void setLine(boolean value) throws PigpioException {
		if (value) {
			setMode(Utils.setBit(getMode(), SM_LINE1_B));
		} else {
			setMode(Utils.clearBit(getMode(), SM_LINE1_B));
		}
	} // End of setLine
	
	/**
	 * Enable or disable the test mode of the device.
	 * @param mode True to enable the test mode and false to disable.
	 * @throws PigpioException
	 */
	public void setTestMode(boolean mode) throws PigpioException {
		if (mode) {
			setMode(Utils.setBit(getMode(), SM_TESTS_B));
		} else {
			setMode(Utils.clearBit(getMode(), SM_TESTS_B));
		}
	} // End of setTestMode

	/**
	 * Wait for the DREQ to signal that the device is ready for more work.
	 * @throws PigpioException
	 */
	public void waitForReady() throws PigpioException {
		while (!isReady()) {
			// Loop
			//pigpio.gpioDelay(5, JPigpio.PI_MILLISECONDS);
		}
	} // End of waitForReady

	/**
	 * Determine if the DREQ flags that the device is ready for more work.
	 * @return True if the device is ready for more work.
	 * @throws PigpioException
	 */
	private boolean isReady() throws PigpioException {
		// The DREQ pin goes low when the device is busy and high when it is idle.
		return pigpio.gpioRead(gpioDREQ);
	} // End of isReady

	/**
	 * Perform a soft reset of the device.
	 * @throws PigpioException
	 */
	public void softReset() throws PigpioException {
		// The device can be "soft reset" by setting the SM_RESET bit of the mode register.
		setMode(Utils.setBit(getMode(), SM_RESET_B));
	} // End of softRest

	/**
	 * Format a data value into a string.  The data value will be data returned from one of
	 * the registers that can be formated.
	 * @param value The value of a register.
	 * @param type The type of the register that is to be formatted.  One of:
	 * <ul>
	 * <li>AUDATA</li>
	 * <li>MODE</li>
	 * </ul>
	 * @return
	 */
	public String format(int value, String type) {
		long unsignedData = Integer.toUnsignedLong(value);
		String ret = "";
		switch (type) {
		case "AUDATA":
			if (Utils.isSet(value, 0)) {
				ret += "Stereo";
			} else {
				ret += "Mono";
			}
			return String.format("(0x%x): %dHz %s", unsignedData, (unsignedData & ~0b1), ret);
		case "MODE":
			if (Utils.isSet(value, SM_DIFF_B)) {
				ret += "Left_Channel_Inverted";
			} else {
				ret += "Normal_in_phase_audio";
			}
			if (Utils.isSet(value, SM_LAYER12_B)) {
				ret += " Allow_MPEG_1&2";
			}
			if (Utils.isSet(value, SM_TESTS_B)) {
				ret += " SDI_Tests_allowed";
			}
			if (Utils.isSet(value, SM_STREAM_B)) {
				ret += " Streaming";
			}
			if (Utils.isSet(value, SM_DACT_B)) {
				ret += " DCLK_Active_Edge_Falling";
			} else {
				ret += " DCLK_Active_Edge_Rising";
			}
			if (Utils.isSet(value, SM_SDIORD_B)) {
				ret += " LSB_First";
			} else {
				ret += " MSB_First";
			}
			if (Utils.isSet(value, SM_SDISHARE_B)) {
				ret += " Share_SPI_Select";
			}
			if (Utils.isSet(value, SM_SDINEW_B)) {
				ret += " SDI_New";
			}
			if (Utils.isSet(value, SM_ADPCM_B)) {
				ret += " PCM/ADPCM_Recording_Active";
			}
			if (Utils.isSet(value, SM_LINE1_B)) {
				ret += " LINE1";
			} else {
				ret += " MICP";
			}
			if (Utils.isSet(value, SM_CLK_RANGE_B)) {
				ret += " 24..26MHz";
			} else {
				ret += " 12..13MHz";
			}
			return ret;

		default:
			System.out.println("Unknown formatting type: " + type);
			return Utils.int16ToBinary(value);
		}
	} // End of format

	/**
	 * Perform the sine test
	 * @throws PigpioException
	 */
	public void startSineTest() throws PigpioException {
		byte data[] = { 0x53, (byte) 0xEF, 0x63, 126, 0, 0, 0, 0 };
		writeSdi(data);
	} // End of startSineTest

	/**
	 * End the sine test
	 * @throws PigpioException
	 */
	public void endSineTest() throws PigpioException {
		byte data[] = { 0x45, (byte) 0x78, 0x69, 0x74, 0, 0, 0, 0 };
		writeSdi(data);
	} // End of endSineTest

	
	/**
	 * Perform a memory test
	 * @throws PigpioException
	 */
	public void memoryTest() throws PigpioException {
		byte data[] = { 0x4D, (byte) 0xEA, 0x6D, 0x54, 0, 0, 0, 0 };
		writeSdi(data);
		pigpio.gpioDelay(5, JPigpio.PI_SECONDS);
		int val = readSci(SCI_HDAT0);
		System.out.println(String.format("memoryTest: 0x%x", val));
	} // End of memoryTest



	/**
	 * Set the address pointer for the next read or write of Ram operation.
	 * @param address The address to be used to set the Ram pointer.
	 * @throws PigpioException
	 */
	private void setRWAddress(int address) throws PigpioException {
		writeSci(SCI_WRAMADDR, address);
	} // End of setRWAddress

	/**
	 * Read 16 bits of data from a specific Ram address.
	 * @param address The address of memory from which to read Ram.
	 * @return 16 bits of data read from the Ram address.
	 * @throws PigpioException
	 */
	private int readRamFromAddress(int address) throws PigpioException {
		setRWAddress(address);
		return readRam();
	} // End of readRamFromAddress

	/**
	 * Write 16 bits of Ram data to the current Ram address.
	 * @param value 16 bits of data.
	 * @throws PigpioException
	 */
	private void writeRam(int value) throws PigpioException {
		writeSci(SCI_WRAM, value);
	} // End of writeRam

	/**
	 * Read Ram from the current Ram pointer.
	 * @return 16 bits of Ram data.
	 * @throws PigpioException
	 */
	private int readRam() throws PigpioException {
		return readSci(SCI_WRAM);
	} // End of readRam

	/**
	 * The LCtech board contains a design flaw. The GPIO 0 and 1 pins are not connected and appear to float high. What this means is that the device enters Midi mode on boot.
	 * Correctly, these pins should be set low. One way to achieve this is to physically solder the pins but that is difficult. An alternate is the recipe found in the forum which
	 * is to set the GPIO pins to output and explicitly set their values to 0 (low).
	 * 
	 * See the following for details: http://www.bajdi.com/lcsoft-vs1053-mp3-module/
	 * 
	 * @throws PigpioException
	 */
	public void disableMidi() throws PigpioException {
		// Get current values
		int value = getAudata();
		if (value != 0xac45) {
			System.out.println("Skipping midi setup");
			return;
		}
		setRWAddress(0xc017);
		value = readSci(SCI_WRAM);
		System.out.println(String.format("Current GPIO direction: %s", Utils.int16ToBinary(value)));
		setRWAddress(0xc019);
		value = readSci(SCI_WRAM);
		System.out.println(String.format("Current GPIO values: %s", Utils.int16ToBinary(value)));
		setRWAddress(0xc017);
		writeRam(0b11);
		setRWAddress(0xc019);
		writeRam(0b00);
		softReset();
	} // End of disableMidi

	public void dump() throws PigpioException {
		int mode = getMode();
		System.out.println("Mode: " + Utils.int16ToBinary(mode));
		System.out.println("Mode: " + format(mode, "MODE"));
		System.out.println("Status: " + Utils.int16ToBinary(getStatus()));

		int audata = getAudata();
		System.out.println("Audata: " + Utils.int16ToBinary(audata));
		System.out.println("Audata: " + format(audata, "AUDATA"));
		System.out.println("Volume: " + Utils.int16ToBinary(getVolume()));
		System.out.println("Bass: " + Utils.int16ToBinary(getBass()));
		System.out.println("ClockF: " + Utils.int16ToBinary(getClockF()));
		Parameters parameters = new Parameters();
		parameters.retrieve();
		System.out.println("Parameters: " + parameters);
		System.out.println("---");
	} // End of dump

	/**
	 * Play a data file through the device.
	 * @param file The file to play.
	 * @throws PigpioException
	 */
	public void playFile(File file) throws PigpioException {
		FileIO fileIO = new FileIO(file);
		long start = pigpio.gpioTick();
		try {
			System.out.println("Playing audio file: " + file);
			while (true) {
				byte data[] = fileIO.read(32);
				if (data.length == 0) {
					break;
				}
				writeSdi(data);
				if ((pigpio.gpioTick() - start) > (5 * 1000 * 1000)) {
					dump();
					start = pigpio.gpioTick();
				}
			}
			fileIO.close();
			System.out.println("End of playing audio file.");
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("End of audio ...");
	} // End of playFile
} // End of class
// End of file