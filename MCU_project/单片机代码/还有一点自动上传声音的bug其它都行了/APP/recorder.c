#include "recorder.h"
#include "delay.h"
#include "usart.h"
#include "key.h"	  
#include "led.h"	  
#include "lcd.h"	    
#include "vs10xx.h"	  
#include "malloc.h"
#include "ff.h"
#include "exfuns.h"	    
#include "text.h"	    
#include "tpad.h"	    
#include "fattester.h"	
#include "common.h"	
//////////////////////////////////////////////////////////////////////////////////	 
//±¾³ÌĞòÖ»¹©Ñ§Ï°Ê¹ÓÃ£¬Î´¾­×÷ÕßĞí¿É£¬²»µÃÓÃÓÚÆäËüÈÎºÎÓÃÍ¾
//ALIENTEKÕ½½¢STM32¿ª·¢°åV3
//wavÂ¼ÒôÇı¶¯ ´úÂë	   
//ÕıµãÔ­×Ó@ALIENTEK
//¼¼ÊõÂÛÌ³:www.openedv.com
//´´½¨ÈÕÆÚ:2015/1/21
//°æ±¾£ºV1.0
//°æÈ¨ËùÓĞ£¬µÁ°æ±Ø¾¿¡£
//Copyright(C) ¹ãÖİÊĞĞÇÒíµç×Ó¿Æ¼¼ÓĞÏŞ¹«Ë¾ 2009-2019
//All rights reserved								  						    								  
//////////////////////////////////////////////////////////////////////////////////
	

//VS1053µÄWAVÂ¼ÒôÓĞbug,Õâ¸öplugin¿ÉÒÔĞŞÕıÕâ¸öÎÊÌâ 							    
const u16 wav_plugin[40]=/* Compressed plugin */ 
{ 
0x0007, 0x0001, 0x8010, 0x0006, 0x001c, 0x3e12, 0xb817, 0x3e14, /* 0 */ 
0xf812, 0x3e01, 0xb811, 0x0007, 0x9717, 0x0020, 0xffd2, 0x0030, /* 8 */ 
0x11d1, 0x3111, 0x8024, 0x3704, 0xc024, 0x3b81, 0x8024, 0x3101, /* 10 */ 
0x8024, 0x3b81, 0x8024, 0x3f04, 0xc024, 0x2808, 0x4800, 0x36f1, /* 18 */ 
0x9811, 0x0007, 0x0001, 0x8028, 0x0006, 0x0002, 0x2a00, 0x040e,  
}; 
//¼¤»îPCM Â¼ÒôÄ£Ê½
//agc:0,×Ô¶¯ÔöÒæ.1024Ïàµ±ÓÚ1±¶,512Ïàµ±ÓÚ0.5±¶,×î´óÖµ65535=64±¶		  
void recoder_enter_rec_mode(u16 agc)
{
	//Èç¹ûÊÇIMA ADPCM,²ÉÑùÂÊ¼ÆËã¹«Ê½ÈçÏÂ:
 	//²ÉÑùÂÊ=CLKI/256*d;	
	//¼ÙÉèd=0,²¢2±¶Æµ,Íâ²¿¾§ÕñÎª12.288M.ÄÇÃ´Fc=(2*12288000)/256*6=16Khz
	//Èç¹ûÊÇÏßĞÔPCM,²ÉÑùÂÊÖ±½Ó¾ÍĞ´²ÉÑùÖµ 
   	VS_WR_Cmd(SPI_BASS,0x0000);    
 	VS_WR_Cmd(SPI_AICTRL0,8000);	//ÉèÖÃ²ÉÑùÂÊ,ÉèÖÃÎª8Khz
 	VS_WR_Cmd(SPI_AICTRL1,agc);		//ÉèÖÃÔöÒæ,0,×Ô¶¯ÔöÒæ.1024Ïàµ±ÓÚ1±¶,512Ïàµ±ÓÚ0.5±¶,×î´óÖµ65535=64±¶	
 	VS_WR_Cmd(SPI_AICTRL2,0);		//ÉèÖÃÔöÒæ×î´óÖµ,0,´ú±í×î´óÖµ65536=64X
 	VS_WR_Cmd(SPI_AICTRL3,6);		//×óÍ¨µÀ(MICµ¥ÉùµÀÊäÈë)
	VS_WR_Cmd(SPI_CLOCKF,0X2000);	//ÉèÖÃVS10XXµÄÊ±ÖÓ,MULT:2±¶Æµ;ADD:²»ÔÊĞí;CLK:12.288Mhz
	VS_WR_Cmd(SPI_MODE,0x1804);		//MIC,Â¼Òô¼¤»î    
 	delay_ms(5);					//µÈ´ıÖÁÉÙ1.35ms 
 	VS_Load_Patch((u16*)wav_plugin,40);//VS1053µÄWAVÂ¼ÒôĞèÒªpatch
}

//³õÊ¼»¯WAVÍ·.
void recoder_wav_init(__WaveHeader* wavhead) //³õÊ¼»¯WAVÍ·			   
{
	wavhead->riff.ChunkID=0X46464952;	//"RIFF"
	wavhead->riff.ChunkSize=0;			//»¹Î´È·¶¨,×îºóĞèÒª¼ÆËã
	wavhead->riff.Format=0X45564157; 	//"WAVE"
	wavhead->fmt.ChunkID=0X20746D66; 	//"fmt "
	wavhead->fmt.ChunkSize=16; 			//´óĞ¡Îª16¸ö×Ö½Ú
	wavhead->fmt.AudioFormat=0X01; 		//0X01,±íÊ¾PCM;0X01,±íÊ¾IMA ADPCM
 	wavhead->fmt.NumOfChannels=1;		//µ¥ÉùµÀ
 	wavhead->fmt.SampleRate=8000;		//8Khz²ÉÑùÂÊ ²ÉÑùËÙÂÊ
 	wavhead->fmt.ByteRate=wavhead->fmt.SampleRate*2;//16Î»,¼´2¸ö×Ö½Ú
 	wavhead->fmt.BlockAlign=2;			//¿é´óĞ¡,2¸ö×Ö½ÚÎªÒ»¸ö¿é
 	wavhead->fmt.BitsPerSample=16;		//16Î»PCM
   	wavhead->data.ChunkID=0X61746164;	//"data"
 	wavhead->data.ChunkSize=0;			//Êı¾İ´óĞ¡,»¹ĞèÒª¼ÆËã  
}
							    
//ÏÔÊ¾Â¼ÒôÊ±³¤
//x,y:µØÖ·
//tsec:ÃëÖÓÊı.
void recoder_show_time(u32 tsec)
{   
	//ÏÔÊ¾Â¼ÒôÊ±¼ä			 
	LCD_ShowString(30,250,200,16,16,"TIME:");	  	  
	LCD_ShowxNum(30+40,250,tsec/60,2,16,0X80);	//·ÖÖÓ
	LCD_ShowChar(30+56,250,':',16,0);
	LCD_ShowxNum(30+64,250,tsec%60,2,16,0X80);	//ÃëÖÓ		
}  	   

//Í¨¹ıÊ±¼ä»ñÈ¡ÎÄ¼şÃû
//½öÏŞÔÚSD¿¨±£´æ,²»Ö§³ÖFLASH DISK±£´æ
//×éºÏ³É:ĞÎÈç"0:RECORDER/REC20120321210633.wav"µÄÎÄ¼şÃû
char * get_now_time(void)
{	 
	int rlen = 0;
	u8 *p;
	p=mymalloc(SRAMIN,30);
	Show_Str(30+34,115,156,12,"go into get_now_time",12,0);
	sendStr((char*)("{\"from\":1,\"order\":\"010\"}"));
	//Show_Str(30+34,115,156,12,"go into get_now_time",12,0); 			//ÏÔÊ¾½ÓÊÕµ½µÄÊı¾İ³¤¶È
	while(!(USART3_RX_STA&0X8000));
	USART3_RX_STA = 0;
	while(!(USART3_RX_STA&0X8000));
	
	if(USART3_RX_STA&0X8000)		//½ÓÊÕµ½Ò»´ÎÊı¾İÁË
				{ 
					Show_Str(30,115,156,12, "go into recive data",12,0); 			//ÏÔÊ¾½ÓÊÕµ½µÄÊı¾İ³¤¶È
					rlen=USART3_RX_STA&0X7FFF;	//µÃµ½±¾´Î½ÓÊÕµ½µÄÊı¾İ³¤¶È
					USART3_RX_BUF[rlen]=0;		//Ìí¼Ó½áÊø·û 
					printf("%s",USART3_RX_BUF);	//·¢ËÍµ½´®¿Ú   
					sprintf((char*)p,"ÊÕµ½%d×Ö½Ú,ÄÚÈİÈçÏÂ",rlen);//½ÓÊÕµ½µÄ×Ö½ÚÊı 
					LCD_Fill(30+54,400,239,130,WHITE);
					POINT_COLOR=BRED;
					Show_Str(0,400,156,12,p,12,0); 			//ÏÔÊ¾½ÓÊÕµ½µÄÊı¾İ³¤¶È
					POINT_COLOR=BLUE;
					LCD_Fill(0,430,239,319,WHITE);
					Show_Str(0,430,180,190,USART3_RX_BUF,12,0);//ÏÔÊ¾½ÓÊÕµ½µÄÊı¾İ  
					USART3_RX_STA=0;
					
				}  
	Show_Str(30+54,115,156,12,"quit get_now_time",12,0); 			//ÏÔÊ¾½ÓÊÕµ½µÄÊı¾İ³¤¶
	myfree(SRAMIN,p);
	return (char*)USART3_RX_BUF;
	
}


char * get_wav_time(void)
{	 
	int rlen = 0;
	u8 *p;
	p=mymalloc(SRAMIN,30);
	Show_Str(30+34,115,156,12,"go into get_now_time",12,0);
	sendStr((char*)("{\"from\":1,\"order\":\"012\"}"));
	//Show_Str(30+34,115,156,12,"go into get_now_time",12,0); 			//ÏÔÊ¾½ÓÊÕµ½µÄÊı¾İ³¤¶È
	while(!(USART3_RX_STA&0X8000));
	USART3_RX_STA = 0;
	while(!(USART3_RX_STA&0X8000));
	
	if(USART3_RX_STA&0X8000)		//½ÓÊÕµ½Ò»´ÎÊı¾İÁË
				{ 
					Show_Str(30,115,156,12, "go into recive data",12,0); 			//ÏÔÊ¾½ÓÊÕµ½µÄÊı¾İ³¤¶È
					rlen=USART3_RX_STA&0X7FFF;	//µÃµ½±¾´Î½ÓÊÕµ½µÄÊı¾İ³¤¶È
					USART3_RX_BUF[rlen]='\0';		//Ìí¼Ó½áÊø·û 
					printf("%s",USART3_RX_BUF);	//·¢ËÍµ½´®¿Ú   
					sprintf((char*)p,"ÊÕµ½%d×Ö½Ú,ÄÚÈİÈçÏÂ",rlen);//½ÓÊÕµ½µÄ×Ö½ÚÊı 
					LCD_Fill(30+54,400,239,130,WHITE);
					POINT_COLOR=BRED;
					Show_Str(0,400,156,12,p,12,0); 			//ÏÔÊ¾½ÓÊÕµ½µÄÊı¾İ³¤¶È
					POINT_COLOR=BLUE;
					LCD_Fill(0,430,239,319,WHITE);
					Show_Str(0,430,180,190,USART3_RX_BUF,12,0);//ÏÔÊ¾½ÓÊÕµ½µÄÊı¾İ  
					USART3_RX_STA=0;
					
				}  
	Show_Str(30+54,115,156,12,"quit get_now_time",12,0); 			//ÏÔÊ¾½ÓÊÕµ½µÄÊı¾İ³¤¶
	myfree(SRAMIN,p);
	return (char*)USART3_RX_BUF;
	
}
// send newest wav file
int send_wav()
{
	char* time;
	int flag1 =0;
	u8 * pname;
	pname = mymalloc(SRAMIN,30);
	time = mymalloc(SRAMIN,20);
	sprintf((char*)time,"%s",get_wav_time());
	//printf("get_wav_time:%s", time);
	Show_Str(30+54,500,156,12,(u8*)time,12,0);
	flag1 = atoi(time);
	if (flag1 == -1)
		return 0;
	sprintf((char*)pname,"0:RECORDER/%s",time);
	//printf("TcpSendWav:%s", pname);
	TcpSendWav(pname);
	myfree(SRAMIN,pname);
	myfree(SRAMIN,time);
	return 1;

}
u8* get_send_filename(u8 * path, u8 * FileName)
{
	FRESULT res;	  
    char *fn;   /* This function is assuming non-Unicode cfg. */
#if _USE_LFN
 	fileinfo.lfsize = _MAX_LFN * 2 + 1;
	fileinfo.lfname = mymalloc(SRAMIN,fileinfo.lfsize);
#endif		  

    res = f_opendir(&dir,(const TCHAR*)path); //´ò¿ªÒ»¸öÄ¿Â¼
    if (res == FR_OK) 
	{	
		printf("\r\n"); 
		while(1)
		{
	        res = f_readdir(&dir, &fileinfo);                   //¶ÁÈ¡Ä¿Â¼ÏÂµÄÒ»¸öÎÄ¼ş
	        if (res != FR_OK || fileinfo.fname[0] == 0) break;  //´íÎóÁË/µ½Ä©Î²ÁË,ÍË³ö
	        //if (fileinfo.fname[0] == '.') continue;             //ºöÂÔÉÏ¼¶Ä¿Â¼
#if _USE_LFN
        	fn = *fileinfo.lfname ? fileinfo.lfname : fileinfo.fname;
#else							   
        	fn = fileinfo.fname;
#endif	                                              /* It is a file. */
			printf("%s/", path);//´òÓ¡Â·¾¶	
			printf("%s\r\n",  fn);//´òÓ¡ÎÄ¼şÃû	  
			if (strcmp(fn,(char *)FileName)>0)
			{
				return (u8 *)fn;
			}
		} 
    }	  
	myfree(SRAMIN,fileinfo.lfname);
    return (u8 *)"";	  
}
char * GetWavName()
{
	u8* path = (u8*)"0:/RECORDER";
	char * time;
	char * p ,*name;
	u8 * xianshi;
	xianshi = mymalloc(SRAMIN,50);
	p = mymalloc(SRAMIN,50);
	time = get_wav_time();
	sprintf((char*)xianshi,"wav time:%s",time);
	Show_Str(30,230,440,16,xianshi,16,0);
	sprintf((char*)p,"0:RECORDER/%s",time);
	name = (char *)get_send_filename(path,(u8 *)p);
	myfree(SRAMIN,p);
	printf("out:%s",name);
	myfree(SRAMIN,xianshi);
	return name;
}

//Í¨¹ıÊ±¼ä»ñÈ¡ÎÄ¼şÃû
//½öÏŞÔÚSD¿¨±£´æ,²»Ö§³ÖFLASH DISK±£´æ
//×éºÏ³É:ĞÎÈç"0:RECORDER/REC20120321210633.wav"µÄÎÄ¼şÃû
void recoder_new_pathname(u8 *pname)
{	 
	u8 res;					 
	u16 index=0;
	while(index<0XFFFF)
	{
		sprintf((char*)pname,"0:RECORDER/REC%05d.wav",index);
		res=f_open(ftemp,(const TCHAR*)pname,FA_READ);//³¢ÊÔ´ò¿ªÕâ¸öÎÄ¼ş
		if(res==FR_NO_FILE)break;		//¸ÃÎÄ¼şÃû²»´æÔÚ=ÕıÊÇÎÒÃÇĞèÒªµÄ.
		index++;
	}
}
//ÏÔÊ¾AGC´óĞ¡
//x,y:×ø±ê
//agc:ÔöÒæÖµ 1~15,±íÊ¾1~15±¶;0,±íÊ¾×Ô¶¯ÔöÒæ
void recoder_show_agc(u8 agc)
{  
	LCD_ShowString(30+110,250,200,16,16,"AGC:    ");	  	//ÏÔÊ¾Ãû³Æ,Í¬Ê±Çå³şÉÏ´ÎµÄÏÔÊ¾	  
	if(agc==0)LCD_ShowString(30+142,250,200,16,16,"AUTO");	//×Ô¶¯agc	  	  
	else LCD_ShowxNum(30+142,250,agc,2,16,0X80);			//ÏÔÊ¾AGCÖµ	 
} 

void sendwav(u8 *pname)
{
FIL* fmp3;
    u16 br;
	int j;
	u8 res,rval=0;	  
	u8 *databuf;	   		   
	u16 i=0; 	
	int count = 0;
	fmp3=(FIL*)mymalloc(SRAMIN,sizeof(FIL));//ÉêÇëÄÚ´æ
	databuf=(u8*)mymalloc(SRAMIN,1);		//¿ª±Ù512×Ö½ÚµÄÄÚ´æÇøÓò
	if(databuf==NULL||fmp3==NULL)rval=0XFF ;//ÄÚ´æÉêÇëÊ§°Ü.
	if(rval==0)
	{		
		res=f_open(fmp3,(const TCHAR*)pname,FA_READ);//´ò¿ªÎÄ¼ş	 
 		if(res==0)//´ò¿ª³É¹¦.
		{ 
			VS_SPI_SpeedHigh();	//¸ßËÙ						   
			while(rval==0)
			{
				res=f_read(fmp3,databuf,1,(UINT*)&br);//¶Á³ö4096¸ö×Ö½Ú		
				printf("%02x ",databuf[0]);
				count ++;
				if(count>100)
				{
					count = 0;
					printf("\r\n");
				}
				if(br!=1||res!=0)
				{
					rval=0;
					break;//¶ÁÍêÁË.		  
				} 							 
			}
			f_close(fmp3);
		}else rval=0XFF;//³öÏÖ´íÎó
	}		
  myfree(SRAMIN,fmp3);
	myfree(SRAMIN,databuf);  	 		  	    
}
//²¥·ÅpnameÕâ¸öwavÎÄ¼ş£¨Ò²¿ÉÒÔÊÇMP3µÈ£©		 
u8 rec_play_wav(u8 *pname)
{	 
 	FIL* fmp3;
    u16 br;
	u8 res,rval=0;	  
	u8 *databuf;	   		   
	u16 i=0; 	 		  
	fmp3=(FIL*)mymalloc(SRAMIN,sizeof(FIL));//ÉêÇëÄÚ´æ
	databuf=(u8*)mymalloc(SRAMIN,512);		//¿ª±Ù512×Ö½ÚµÄÄÚ´æÇøÓò
	if(databuf==NULL||fmp3==NULL)rval=0XFF ;//ÄÚ´æÉêÇëÊ§°Ü.
	if(rval==0)
	{	  
		VS_HD_Reset();		   	//Ó²¸´Î»
		VS_Soft_Reset();  		//Èí¸´Î» 
		VS_Set_All();			//ÉèÖÃÒôÁ¿µÈ²ÎÊı 			 
		VS_Reset_DecodeTime();	//¸´Î»½âÂëÊ±¼ä 	  	 
		res=f_open(fmp3,(const TCHAR*)pname,FA_READ);//´ò¿ªÎÄ¼ş	 
 		if(res==0)//´ò¿ª³É¹¦.
		{ 
			VS_SPI_SpeedHigh();	//¸ßËÙ						   
			while(rval==0)
			{
				res=f_read(fmp3,databuf,512,(UINT*)&br);//¶Á³ö4096¸ö×Ö½Ú  
				i=0;
				do//Ö÷²¥·ÅÑ­»·
			    {  	
					if(VS_Send_MusicData(databuf+i)==0)i+=32;//¸øVS10XX·¢ËÍÒôÆµÊı¾İ
				 	else recoder_show_time(VS_Get_DecodeTime());//ÏÔÊ¾²¥·ÅÊ±¼ä	   	    
				}while(i<512);//Ñ­»··¢ËÍ4096¸ö×Ö½Ú 
				if(br!=512||res!=0)
				{
					rval=0;
					break;//¶ÁÍêÁË.		  
				} 							 
			}
			f_close(fmp3);
		}else rval=0XFF;//³öÏÖ´íÎó
		VS_SPK_Set(0);	//¹Ø±Õ°åÔØÀ®°È 
	}		
  myfree(SRAMIN,fmp3);
	myfree(SRAMIN,databuf);
	return rval;	  	 		  	    
}	
char* record_voice(int time)
{
	
	u8 res;
	int status = 0;
	char * reciveTemp;
	int rlen = 0;
	u8 *p;
	u8 key;
	u8 rval=0;
	__WaveHeader *wavhead=0;
	u32 sectorsize=0;
	FIL* f_rec=0;					//ÎÄ¼ş		    
 	DIR recdir;	 					//Ä¿Â¼
	u8 *recbuf;						//Êı¾İÄÚ´æ	 
 	u16 w;
	u16 idx=0;	    
	u8 rec_sta=0;					//Â¼Òô×´Ì¬
									//[7]:0,Ã»ÓĞÂ¼Òô;1,ÓĞÂ¼Òô;
									//[6:1]:±£Áô
									//[0]:0,ÕıÔÚÂ¼Òô;1,ÔİÍ£Â¼Òô;
 	u8 *pname=0;
	u8 timecnt=0;					//¼ÆÊ±Æ÷   
	u32 recsec=0;					//Â¼ÒôÊ±¼ä
 	u8 recagc=4;					//Ä¬ÈÏÔöÒæÎª4
	Show_Str(30,230,440,16,"go into record_voice",16,0);	//ÏÔÊ¾µ±Ç°Â¼ÒôÎÄ¼şÃû×Ö
  while(f_opendir(&recdir,"0:/RECORDER"))//´ò¿ªÂ¼ÒôÎÄ¼ş¼Ğ
 	{	 
		Show_Str(30,230,240,16,"RECORDERÎÄ¼ş¼Ğ´íÎó!",16,0);
		delay_ms(200);				  
		LCD_Fill(30,230,240,246,WHITE);		//Çå³ıÏÔÊ¾	     
		delay_ms(200);				  
		f_mkdir("0:/RECORDER");				//´´½¨¸ÃÄ¿Â¼   
	} 
  f_rec=(FIL *)mymalloc(SRAMIN,sizeof(FIL));	//¿ª±ÙFIL×Ö½ÚµÄÄÚ´æÇøÓò 
	if(f_rec==NULL)rval=1;	//ÉêÇëÊ§°Ü
 	wavhead=(__WaveHeader*)mymalloc(SRAMIN,sizeof(__WaveHeader));//¿ª±Ù__WaveHeader×Ö½ÚµÄÄÚ´æÇøÓò
	if(wavhead==NULL)rval=1; 
	recbuf=mymalloc(SRAMIN,512); 	
	if(recbuf==NULL)rval=1;	  		   
	pname=mymalloc(SRAMIN,30);					//ÉêÇë30¸ö×Ö½ÚÄÚ´æ,ÀàËÆ"0:RECORDER/REC00001.wav"
	p=mymalloc(SRAMIN,30);
	if(pname==NULL)rval=1;
 	if(rval==0)									//ÄÚ´æÉêÇëOK
	{      
 		recoder_enter_rec_mode(1024*recagc);				
   		while(VS_RD_Reg(SPI_HDAT1)>>8);			//µÈµ½buf ½ÏÎª¿ÕÏĞÔÙ¿ªÊ¼  
  		recoder_show_time(recsec);				//ÏÔÊ¾Ê±¼ä
		recoder_show_agc(recagc);				//ÏÔÊ¾agc
		pname[0]=0;								//pnameÃ»ÓĞÈÎºÎÎÄ¼şÃû		 
 	   	while(rval==0)
		{
			switch(status)
			{		
				case 1:	//STOP&SAVE
					if(rec_sta&0X80)//ÓĞÂ¼Òô
					{
						wavhead->riff.ChunkSize=sectorsize*512+36;	//Õû¸öÎÄ¼şµÄ´óĞ¡-8;
				   		wavhead->data.ChunkSize=sectorsize*512;		//Êı¾İ´óĞ¡
						f_lseek(f_rec,0);							//Æ«ÒÆµ½ÎÄ¼şÍ·.
				  		f_write(f_rec,(const void*)wavhead,sizeof(__WaveHeader),&bw);//Ğ´ÈëÍ·Êı¾İ
						f_close(f_rec);
						sectorsize=0;
					}
					rec_sta=0;
					recsec=0;
				 	LED1=1;	 						//¹Ø±ÕDS1
					LCD_Fill(30,230,240,246,WHITE);	//Çå³ıÏÔÊ¾,Çå³ıÖ®Ç°ÏÔÊ¾µÄÂ¼ÒôÎÄ¼şÃû	     
					recoder_show_time(recsec);		//ÏÔÊ¾Ê±¼ä
					rval = 1;
					break;	 
				case 0:	//REC/PAUSE
					{
	 					rec_sta|=0X80;	//¿ªÊ¼Â¼Òô	 	 
						reciveTemp  = get_now_time();
						sprintf((char*)pname,"0:RECORDER/%s.wav",reciveTemp);
						Show_Str(30,230,240,16,pname+11,16,0);	//ÏÔÊ¾µ±Ç°Â¼ÒôÎÄ¼şÃû×Ö
				 		recoder_wav_init(wavhead);				//³õÊ¼»¯wavÊı¾İ	
	 					res=f_open(f_rec,(const TCHAR*)pname, FA_CREATE_ALWAYS | FA_WRITE); 
						if(res)			//ÎÄ¼ş´´½¨Ê§°Ü
						{
							rec_sta=0;	//´´½¨ÎÄ¼şÊ§°Ü,²»ÄÜÂ¼Òô
							rval=0XFE;	//ÌáÊ¾ÊÇ·ñ´æÔÚSD¿¨
						}else 
						{
							res=f_write(f_rec,(const void*)wavhead,sizeof(__WaveHeader),&bw);//Ğ´ÈëÍ·Êı¾İ
						}
						
	 				}
					status = -1;
					break;
			} 
///////////////////////////////////////////////////////////
//¶ÁÈ¡Êı¾İ			  
			if(rec_sta==0X80)//ÒÑ¾­ÔÚÂ¼ÒôÁË
			{
		  		w=VS_RD_Reg(SPI_HDAT1);	
				if((w>=256)&&(w<896))
				{
	 				idx=0;				   	 
		  			while(idx<512) 	//Ò»´Î¶ÁÈ¡512×Ö½Ú
					{	 
			 			w=VS_RD_Reg(SPI_HDAT0);				   	    
		 				recbuf[idx++]=w&0XFF;
						recbuf[idx++]=w>>8;
					}	  		 
	 				res=f_write(f_rec,recbuf,512,&bw);//Ğ´ÈëÎÄ¼ş
					if(res)
					{
						printf("err:%d\r\n",res);
						printf("bw:%d\r\n",bw);
						break;//Ğ´Èë³ö´í.	  
					}
					sectorsize++;//ÉÈÇøÊıÔö¼Ó1,Ô¼Îª32ms	 
				}			
			}
/////////////////////////////////////////////////////////////
 			if(recsec!=(sectorsize*4/125))//Â¼ÒôÊ±¼äÏÔÊ¾
			{	   
				LED0=!LED0;//DS0ÉÁË¸ 
				recsec=sectorsize*4/125;
				recoder_show_time(recsec);//ÏÔÊ¾Ê±¼ä
				if (recsec >=time)
				{
					status = 1;
				}
			}
		}			
	
	}	   
Show_Str(30+34,200,156,12,"go out recoder_play",12,0);		
	myfree(SRAMIN,wavhead);
	myfree(SRAMIN,recbuf);	  
 	myfree(SRAMIN,f_rec);	 
	myfree(SRAMIN,pname);
	myfree(SRAMIN,p);
	Show_Str(30,230,470,16,"go out record_voice",16,0);	//ÏÔÊ¾µ±Ç°Â¼ÒôÎÄ¼şÃû×Ö
	return (char *)pname;

}

//Â¼Òô»ú
//ËùÓĞÂ¼ÒôÎÄ¼ş,¾ù±£´æÔÚSD¿¨RECORDERÎÄ¼ş¼ĞÄÚ.
u8 recoder_play(void)
{
	u8 res;
	int flag = 1;
	char * reciveTemp;
	int rlen = 0;
	u8 *p;
	u8 key;
	u8 rval=0;
	__WaveHeader *wavhead=0;
	u32 sectorsize=0;
	FIL* f_rec=0;					//ÎÄ¼ş		    
 	DIR recdir;	 					//Ä¿Â¼
	u8 *recbuf;						//Êı¾İÄÚ´æ	 
 	u16 w;
	u16 idx=0;	    
	u8 rec_sta=0;					//Â¼Òô×´Ì¬
									//[7]:0,Ã»ÓĞÂ¼Òô;1,ÓĞÂ¼Òô;
									//[6:1]:±£Áô
									//[0]:0,ÕıÔÚÂ¼Òô;1,ÔİÍ£Â¼Òô;
 	u8 *pname=0;
	u8 timecnt=0;					//¼ÆÊ±Æ÷   
	u32 recsec=0;					//Â¼ÒôÊ±¼ä
 	u8 recagc=4;					//Ä¬ÈÏÔöÒæÎª4
  while(f_opendir(&recdir,"0:/RECORDER"))//´ò¿ªÂ¼ÒôÎÄ¼ş¼Ğ
 	{	 
		Show_Str(30,230,240,16,"RECORDERÎÄ¼ş¼Ğ´íÎó!",16,0);
		delay_ms(200);				  
		LCD_Fill(30,230,240,246,WHITE);		//Çå³ıÏÔÊ¾	     
		delay_ms(200);				  
		f_mkdir("0:/RECORDER");				//´´½¨¸ÃÄ¿Â¼   
	} 
  	f_rec=(FIL *)mymalloc(SRAMIN,sizeof(FIL));	//¿ª±ÙFIL×Ö½ÚµÄÄÚ´æÇøÓò 
	if(f_rec==NULL)rval=1;	//ÉêÇëÊ§°Ü
 	wavhead=(__WaveHeader*)mymalloc(SRAMIN,sizeof(__WaveHeader));//¿ª±Ù__WaveHeader×Ö½ÚµÄÄÚ´æÇøÓò
	if(wavhead==NULL)rval=1; 
	recbuf=mymalloc(SRAMIN,512); 	
	if(recbuf==NULL)rval=1;	  		   
	pname=mymalloc(SRAMIN,30);					//ÉêÇë30¸ö×Ö½ÚÄÚ´æ,ÀàËÆ"0:RECORDER/REC00001.wav"
	p=mymalloc(SRAMIN,30);
	if(pname==NULL)rval=1;
 	if(rval==0)									//ÄÚ´æÉêÇëOK
	{      
 		recoder_enter_rec_mode(1024*recagc);				
   		while(VS_RD_Reg(SPI_HDAT1)>>8);			//µÈµ½buf ½ÏÎª¿ÕÏĞÔÙ¿ªÊ¼  
  		recoder_show_time(recsec);				//ÏÔÊ¾Ê±¼ä
		recoder_show_agc(recagc);				//ÏÔÊ¾agc
		pname[0]=0;								//pnameÃ»ÓĞÈÎºÎÎÄ¼şÃû		 
 	   	while(rval==0)
		{
			key=KEY_Scan(0);
			switch(key)
			{		
				case KEY2_PRES:	//STOP&SAVE
					while(flag)
					{
						sprintf((char*)p,"go into send_wav %d",timecnt);	
						Show_Str(30+34,350,256,12,p,12,0);
						flag = send_wav();
						if (flag == 0)
						{
							
							Show_Str(30+34,500,156,12,(u8*)"Upload in full",12,0);
							
						}
					}
					sprintf((char*)p,"go out send_wav %d",timecnt);	
					Show_Str(30+34,350,256,12,p,12,0);
					break;	 
				case KEY0_PRES:	//REC/PAUSE
					record_voice(3);
					flag = 1;
					break;
				case WKUP_PRES:	//AGC+	 
				case KEY1_PRES:	//AGC-
					if(key==WKUP_PRES)recagc++;
					else if(recagc)recagc--;
					if(recagc>15)recagc=15;				//·¶Î§ÏŞ¶¨Îª0~15.0,×Ô¶¯AGC.ÆäËûAGC±¶Êı										 
					recoder_show_agc(recagc);
					VS_WR_Cmd(SPI_AICTRL1,1024*recagc);	//ÉèÖÃÔöÒæ,0,×Ô¶¯ÔöÒæ.1024Ïàµ±ÓÚ1±¶,512Ïàµ±ÓÚ0.5±¶
					break;
			} 
///////////////////////////////////////////////////////////
//¶ÁÈ¡Êı¾İ			  
			if(rec_sta==0X80)//ÒÑ¾­ÔÚÂ¼ÒôÁË
			{
		  		w=VS_RD_Reg(SPI_HDAT1);	
				if((w>=256)&&(w<896))
				{
	 				idx=0;				   	 
		  			while(idx<512) 	//Ò»´Î¶ÁÈ¡512×Ö½Ú
					{	 
			 			w=VS_RD_Reg(SPI_HDAT0);				   	    
		 				recbuf[idx++]=w&0XFF;
						recbuf[idx++]=w>>8;
					}	  		 
	 				res=f_write(f_rec,recbuf,512,&bw);//Ğ´ÈëÎÄ¼ş
					if(res)
					{
						printf("err:%d\r\n",res);
						printf("bw:%d\r\n",bw);
						break;//Ğ´Èë³ö´í.	  
					}
					sectorsize++;//ÉÈÇøÊıÔö¼Ó1,Ô¼Îª32ms	 
				}			
			}else//Ã»ÓĞ¿ªÊ¼Â¼Òô£¬Ôò¼ì²âTPAD°´¼ü
			{			
				if(TPAD_Scan(0))//Èç¹û´¥Ãş°´¼ü±»°´ÏÂ,ÇÒpname²»Îª¿Õ
				{
					reciveTemp = record_voice(3);
					/*
					sprintf((char*)p,"go into tpad %d",timecnt);	
					Show_Str(30+34,115,156,12,p,12,0); 			//ÏÔÊ¾½ÓÊÕµ½µÄÊı¾İ³¤¶È
					reciveTemp = get_now_time();
					
					Show_Str(30+34,450,156,12,(u8 *) reciveTemp,12,0); 			//ÏÔÊ¾½ÓÊÕµ½µÄÊı¾İ³¤¶È
					
					*/
					/*
					pname = "0:/RECORDER/1620709208";
					Show_Str(30,230,240,16,"²¥·Å:",16,0);		   
					Show_Str(30+40,230,240,16,pname+11,16,0);	//ÏÔÊ¾µ±²¥·ÅµÄÎÄ¼şÃû×Ö   
					TcpSendWav(pname);						//²¥·Åpname
					LCD_Fill(30,230,240,246,WHITE);				//Çå³ıÏÔÊ¾,Çå³ıÖ®Ç°ÏÔÊ¾µÄÂ¼ÒôÎÄ¼şÃû	  
					recoder_enter_rec_mode(1024*recagc);		//ÖØĞÂ½øÈëÂ¼ÒôÄ£Ê½		
			   		while(VS_RD_Reg(SPI_HDAT1)>>8);				//µÈµ½buf ½ÏÎª¿ÕÏĞÔÙ¿ªÊ¼  
			  		recoder_show_time(recsec);					//ÏÔÊ¾Ê±¼ä
					recoder_show_agc(recagc);					//ÏÔÊ¾agc
					*/
					/*
					reciveTemp = GetWavName();
					sprintf((char*)p,"send name %d",(u8 *)reciveTemp);	
					Show_Str(30+34,500,156,12,p,12,0); 			//ÏÔÊ¾½ÓÊÕµ½µÄÊı¾İ³¤¶È
					*/
					
 				}
				sprintf((char*)p,"go out tpad %d",timecnt);	
				Show_Str(30+34,150,156,12,p,12,0);
				
				delay_ms(5);
				timecnt++;
				if((timecnt%20)==0)LED0=!LED0;//DS0ÉÁË¸ 
			}
/////////////////////////////////////////////////////////////
 			if(recsec!=(sectorsize*4/125))//Â¼ÒôÊ±¼äÏÔÊ¾
			{	   
				LED0=!LED0;//DS0ÉÁË¸ 
				recsec=sectorsize*4/125;
				recoder_show_time(recsec);//ÏÔÊ¾Ê±¼ä
			}
		}			
	
	}	   
Show_Str(30+34,200,156,12,"go out recoder_play",12,0);		
	myfree(SRAMIN,wavhead);
	myfree(SRAMIN,recbuf);	  
 	myfree(SRAMIN,f_rec);	 
	myfree(SRAMIN,pname);
	myfree(SRAMIN,p);
	return rval;
}


























