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
//本程序只供学习使用，未经作者许可，不得用于其它任何用途
//ALIENTEK战舰STM32开发板V3
//wav录音驱动 代码	   
//正点原子@ALIENTEK
//技术论坛:www.openedv.com
//创建日期:2015/1/21
//版本：V1.0
//版权所有，盗版必究。
//Copyright(C) 广州市星翼电子科技有限公司 2009-2019
//All rights reserved								  						    								  
//////////////////////////////////////////////////////////////////////////////////
	

//VS1053的WAV录音有bug,这个plugin可以修正这个问题 							    
const u16 wav_plugin[40]=/* Compressed plugin */ 
{ 
0x0007, 0x0001, 0x8010, 0x0006, 0x001c, 0x3e12, 0xb817, 0x3e14, /* 0 */ 
0xf812, 0x3e01, 0xb811, 0x0007, 0x9717, 0x0020, 0xffd2, 0x0030, /* 8 */ 
0x11d1, 0x3111, 0x8024, 0x3704, 0xc024, 0x3b81, 0x8024, 0x3101, /* 10 */ 
0x8024, 0x3b81, 0x8024, 0x3f04, 0xc024, 0x2808, 0x4800, 0x36f1, /* 18 */ 
0x9811, 0x0007, 0x0001, 0x8028, 0x0006, 0x0002, 0x2a00, 0x040e,  
}; 
//激活PCM 录音模式
//agc:0,自动增益.1024相当于1倍,512相当于0.5倍,最大值65535=64倍		  
void recoder_enter_rec_mode(u16 agc)
{
	//如果是IMA ADPCM,采样率计算公式如下:
 	//采样率=CLKI/256*d;	
	//假设d=0,并2倍频,外部晶振为12.288M.那么Fc=(2*12288000)/256*6=16Khz
	//如果是线性PCM,采样率直接就写采样值 
   	VS_WR_Cmd(SPI_BASS,0x0000);    
 	VS_WR_Cmd(SPI_AICTRL0,8000);	//设置采样率,设置为8Khz
 	VS_WR_Cmd(SPI_AICTRL1,agc);		//设置增益,0,自动增益.1024相当于1倍,512相当于0.5倍,最大值65535=64倍	
 	VS_WR_Cmd(SPI_AICTRL2,0);		//设置增益最大值,0,代表最大值65536=64X
 	VS_WR_Cmd(SPI_AICTRL3,6);		//左通道(MIC单声道输入)
	VS_WR_Cmd(SPI_CLOCKF,0X2000);	//设置VS10XX的时钟,MULT:2倍频;ADD:不允许;CLK:12.288Mhz
	VS_WR_Cmd(SPI_MODE,0x1804);		//MIC,录音激活    
 	delay_ms(5);					//等待至少1.35ms 
 	VS_Load_Patch((u16*)wav_plugin,40);//VS1053的WAV录音需要patch
}

//初始化WAV头.
void recoder_wav_init(__WaveHeader* wavhead) //初始化WAV头			   
{
	wavhead->riff.ChunkID=0X46464952;	//"RIFF"
	wavhead->riff.ChunkSize=0;			//还未确定,最后需要计算
	wavhead->riff.Format=0X45564157; 	//"WAVE"
	wavhead->fmt.ChunkID=0X20746D66; 	//"fmt "
	wavhead->fmt.ChunkSize=16; 			//大小为16个字节
	wavhead->fmt.AudioFormat=0X01; 		//0X01,表示PCM;0X01,表示IMA ADPCM
 	wavhead->fmt.NumOfChannels=1;		//单声道
 	wavhead->fmt.SampleRate=8000;		//8Khz采样率 采样速率
 	wavhead->fmt.ByteRate=wavhead->fmt.SampleRate*2;//16位,即2个字节
 	wavhead->fmt.BlockAlign=2;			//块大小,2个字节为一个块
 	wavhead->fmt.BitsPerSample=16;		//16位PCM
   	wavhead->data.ChunkID=0X61746164;	//"data"
 	wavhead->data.ChunkSize=0;			//数据大小,还需要计算  
}
							    
//显示录音时长
//x,y:地址
//tsec:秒钟数.
void recoder_show_time(u32 tsec)
{   
	//显示录音时间			 
	LCD_ShowString(30,250,200,16,16,"TIME:");	  	  
	LCD_ShowxNum(30+40,250,tsec/60,2,16,0X80);	//分钟
	LCD_ShowChar(30+56,250,':',16,0);
	LCD_ShowxNum(30+64,250,tsec%60,2,16,0X80);	//秒钟		
}  	   

//通过时间获取文件名
//仅限在SD卡保存,不支持FLASH DISK保存
//组合成:形如"0:RECORDER/REC20120321210633.wav"的文件名
char * get_now_time(void)
{	 
	int rlen = 0;
	u8 *p;
	p=mymalloc(SRAMIN,30);
	Show_Str(30+34,115,156,12,"go into get_now_time",12,0);
	sendStr((char*)("{\"from\":1,\"order\":\"010\"}"));
	//Show_Str(30+34,115,156,12,"go into get_now_time",12,0); 			//显示接收到的数据长度
	while(!(USART3_RX_STA&0X8000));
	USART3_RX_STA = 0;
	while(!(USART3_RX_STA&0X8000));
	
	if(USART3_RX_STA&0X8000)		//接收到一次数据了
				{ 
					Show_Str(30,115,156,12, "go into recive data",12,0); 			//显示接收到的数据长度
					rlen=USART3_RX_STA&0X7FFF;	//得到本次接收到的数据长度
					USART3_RX_BUF[rlen]=0;		//添加结束符 
					printf("%s",USART3_RX_BUF);	//发送到串口   
					sprintf((char*)p,"收到%d字节,内容如下",rlen);//接收到的字节数 
					LCD_Fill(30+54,400,239,130,WHITE);
					POINT_COLOR=BRED;
					Show_Str(0,400,156,12,p,12,0); 			//显示接收到的数据长度
					POINT_COLOR=BLUE;
					LCD_Fill(0,430,239,319,WHITE);
					Show_Str(0,430,180,190,USART3_RX_BUF,12,0);//显示接收到的数据  
					USART3_RX_STA=0;
					
				}  
	Show_Str(30+54,115,156,12,"quit get_now_time",12,0); 			//显示接收到的数据长�
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
	//Show_Str(30+34,115,156,12,"go into get_now_time",12,0); 			//显示接收到的数据长度
	while(!(USART3_RX_STA&0X8000));
	USART3_RX_STA = 0;
	while(!(USART3_RX_STA&0X8000));
	
	if(USART3_RX_STA&0X8000)		//接收到一次数据了
				{ 
					Show_Str(30,115,156,12, "go into recive data",12,0); 			//显示接收到的数据长度
					rlen=USART3_RX_STA&0X7FFF;	//得到本次接收到的数据长度
					USART3_RX_BUF[rlen]='\0';		//添加结束符 
					printf("%s",USART3_RX_BUF);	//发送到串口   
					sprintf((char*)p,"收到%d字节,内容如下",rlen);//接收到的字节数 
					LCD_Fill(30+54,400,239,130,WHITE);
					POINT_COLOR=BRED;
					Show_Str(0,400,156,12,p,12,0); 			//显示接收到的数据长度
					POINT_COLOR=BLUE;
					LCD_Fill(0,430,239,319,WHITE);
					Show_Str(0,430,180,190,USART3_RX_BUF,12,0);//显示接收到的数据  
					USART3_RX_STA=0;
					
				}  
	Show_Str(30+54,115,156,12,"quit get_now_time",12,0); 			//显示接收到的数据长�
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

    res = f_opendir(&dir,(const TCHAR*)path); //打开一个目录
    if (res == FR_OK) 
	{	
		printf("\r\n"); 
		while(1)
		{
	        res = f_readdir(&dir, &fileinfo);                   //读取目录下的一个文件
	        if (res != FR_OK || fileinfo.fname[0] == 0) break;  //错误了/到末尾了,退出
	        //if (fileinfo.fname[0] == '.') continue;             //忽略上级目录
#if _USE_LFN
        	fn = *fileinfo.lfname ? fileinfo.lfname : fileinfo.fname;
#else							   
        	fn = fileinfo.fname;
#endif	                                              /* It is a file. */
			printf("%s/", path);//打印路径	
			printf("%s\r\n",  fn);//打印文件名	  
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

//通过时间获取文件名
//仅限在SD卡保存,不支持FLASH DISK保存
//组合成:形如"0:RECORDER/REC20120321210633.wav"的文件名
void recoder_new_pathname(u8 *pname)
{	 
	u8 res;					 
	u16 index=0;
	while(index<0XFFFF)
	{
		sprintf((char*)pname,"0:RECORDER/REC%05d.wav",index);
		res=f_open(ftemp,(const TCHAR*)pname,FA_READ);//尝试打开这个文件
		if(res==FR_NO_FILE)break;		//该文件名不存在=正是我们需要的.
		index++;
	}
}
//显示AGC大小
//x,y:坐标
//agc:增益值 1~15,表示1~15倍;0,表示自动增益
void recoder_show_agc(u8 agc)
{  
	LCD_ShowString(30+110,250,200,16,16,"AGC:    ");	  	//显示名称,同时清楚上次的显示	  
	if(agc==0)LCD_ShowString(30+142,250,200,16,16,"AUTO");	//自动agc	  	  
	else LCD_ShowxNum(30+142,250,agc,2,16,0X80);			//显示AGC值	 
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
	fmp3=(FIL*)mymalloc(SRAMIN,sizeof(FIL));//申请内存
	databuf=(u8*)mymalloc(SRAMIN,1);		//开辟512字节的内存区域
	if(databuf==NULL||fmp3==NULL)rval=0XFF ;//内存申请失败.
	if(rval==0)
	{		
		res=f_open(fmp3,(const TCHAR*)pname,FA_READ);//打开文件	 
 		if(res==0)//打开成功.
		{ 
			VS_SPI_SpeedHigh();	//高速						   
			while(rval==0)
			{
				res=f_read(fmp3,databuf,1,(UINT*)&br);//读出4096个字节		
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
					break;//读完了.		  
				} 							 
			}
			f_close(fmp3);
		}else rval=0XFF;//出现错误
	}		
  myfree(SRAMIN,fmp3);
	myfree(SRAMIN,databuf);  	 		  	    
}
//播放pname这个wav文件（也可以是MP3等）		 
u8 rec_play_wav(u8 *pname)
{	 
 	FIL* fmp3;
    u16 br;
	u8 res,rval=0;	  
	u8 *databuf;	   		   
	u16 i=0; 	 		  
	fmp3=(FIL*)mymalloc(SRAMIN,sizeof(FIL));//申请内存
	databuf=(u8*)mymalloc(SRAMIN,512);		//开辟512字节的内存区域
	if(databuf==NULL||fmp3==NULL)rval=0XFF ;//内存申请失败.
	if(rval==0)
	{	  
		VS_HD_Reset();		   	//硬复位
		VS_Soft_Reset();  		//软复位 
		VS_Set_All();			//设置音量等参数 			 
		VS_Reset_DecodeTime();	//复位解码时间 	  	 
		res=f_open(fmp3,(const TCHAR*)pname,FA_READ);//打开文件	 
 		if(res==0)//打开成功.
		{ 
			VS_SPI_SpeedHigh();	//高速						   
			while(rval==0)
			{
				res=f_read(fmp3,databuf,512,(UINT*)&br);//读出4096个字节  
				i=0;
				do//主播放循环
			    {  	
					if(VS_Send_MusicData(databuf+i)==0)i+=32;//给VS10XX发送音频数据
				 	else recoder_show_time(VS_Get_DecodeTime());//显示播放时间	   	    
				}while(i<512);//循环发送4096个字节 
				if(br!=512||res!=0)
				{
					rval=0;
					break;//读完了.		  
				} 							 
			}
			f_close(fmp3);
		}else rval=0XFF;//出现错误
		VS_SPK_Set(0);	//关闭板载喇叭 
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
	FIL* f_rec=0;					//文件		    
 	DIR recdir;	 					//目录
	u8 *recbuf;						//数据内存	 
 	u16 w;
	u16 idx=0;	    
	u8 rec_sta=0;					//录音状态
									//[7]:0,没有录音;1,有录音;
									//[6:1]:保留
									//[0]:0,正在录音;1,暂停录音;
 	u8 *pname=0;
	u8 timecnt=0;					//计时器   
	u32 recsec=0;					//录音时间
 	u8 recagc=4;					//默认增益为4
	Show_Str(30,230,440,16,"go into record_voice",16,0);	//显示当前录音文件名字
  while(f_opendir(&recdir,"0:/RECORDER"))//打开录音文件夹
 	{	 
		Show_Str(30,230,240,16,"RECORDER文件夹错误!",16,0);
		delay_ms(200);				  
		LCD_Fill(30,230,240,246,WHITE);		//清除显示	     
		delay_ms(200);				  
		f_mkdir("0:/RECORDER");				//创建该目录   
	} 
  f_rec=(FIL *)mymalloc(SRAMIN,sizeof(FIL));	//开辟FIL字节的内存区域 
	if(f_rec==NULL)rval=1;	//申请失败
 	wavhead=(__WaveHeader*)mymalloc(SRAMIN,sizeof(__WaveHeader));//开辟__WaveHeader字节的内存区域
	if(wavhead==NULL)rval=1; 
	recbuf=mymalloc(SRAMIN,512); 	
	if(recbuf==NULL)rval=1;	  		   
	pname=mymalloc(SRAMIN,30);					//申请30个字节内存,类似"0:RECORDER/REC00001.wav"
	p=mymalloc(SRAMIN,30);
	if(pname==NULL)rval=1;
 	if(rval==0)									//内存申请OK
	{      
 		recoder_enter_rec_mode(1024*recagc);				
   		while(VS_RD_Reg(SPI_HDAT1)>>8);			//等到buf 较为空闲再开始  
  		recoder_show_time(recsec);				//显示时间
		recoder_show_agc(recagc);				//显示agc
		pname[0]=0;								//pname没有任何文件名		 
 	   	while(rval==0)
		{
			switch(status)
			{		
				case 1:	//STOP&SAVE
					if(rec_sta&0X80)//有录音
					{
						wavhead->riff.ChunkSize=sectorsize*512+36;	//整个文件的大小-8;
				   		wavhead->data.ChunkSize=sectorsize*512;		//数据大小
						f_lseek(f_rec,0);							//偏移到文件头.
				  		f_write(f_rec,(const void*)wavhead,sizeof(__WaveHeader),&bw);//写入头数据
						f_close(f_rec);
						sectorsize=0;
					}
					rec_sta=0;
					recsec=0;
				 	LED1=1;	 						//关闭DS1
					LCD_Fill(30,230,240,246,WHITE);	//清除显示,清除之前显示的录音文件名	     
					recoder_show_time(recsec);		//显示时间
					rval = 1;
					break;	 
				case 0:	//REC/PAUSE
					{
	 					rec_sta|=0X80;	//开始录音	 	 
						reciveTemp  = get_now_time();
						sprintf((char*)pname,"0:RECORDER/%s.wav",reciveTemp);
						Show_Str(30,230,240,16,pname+11,16,0);	//显示当前录音文件名字
				 		recoder_wav_init(wavhead);				//初始化wav数据	
	 					res=f_open(f_rec,(const TCHAR*)pname, FA_CREATE_ALWAYS | FA_WRITE); 
						if(res)			//文件创建失败
						{
							rec_sta=0;	//创建文件失败,不能录音
							rval=0XFE;	//提示是否存在SD卡
						}else 
						{
							res=f_write(f_rec,(const void*)wavhead,sizeof(__WaveHeader),&bw);//写入头数据
						}
						
	 				}
					status = -1;
					break;
			} 
///////////////////////////////////////////////////////////
//读取数据			  
			if(rec_sta==0X80)//已经在录音了
			{
		  		w=VS_RD_Reg(SPI_HDAT1);	
				if((w>=256)&&(w<896))
				{
	 				idx=0;				   	 
		  			while(idx<512) 	//一次读取512字节
					{	 
			 			w=VS_RD_Reg(SPI_HDAT0);				   	    
		 				recbuf[idx++]=w&0XFF;
						recbuf[idx++]=w>>8;
					}	  		 
	 				res=f_write(f_rec,recbuf,512,&bw);//写入文件
					if(res)
					{
						printf("err:%d\r\n",res);
						printf("bw:%d\r\n",bw);
						break;//写入出错.	  
					}
					sectorsize++;//扇区数增加1,约为32ms	 
				}			
			}
/////////////////////////////////////////////////////////////
 			if(recsec!=(sectorsize*4/125))//录音时间显示
			{	   
				LED0=!LED0;//DS0闪烁 
				recsec=sectorsize*4/125;
				recoder_show_time(recsec);//显示时间
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
	Show_Str(30,230,470,16,"go out record_voice",16,0);	//显示当前录音文件名字
	return (char *)pname;

}

//录音机
//所有录音文件,均保存在SD卡RECORDER文件夹内.
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
	FIL* f_rec=0;					//文件		    
 	DIR recdir;	 					//目录
	u8 *recbuf;						//数据内存	 
 	u16 w;
	u16 idx=0;	    
	u8 rec_sta=0;					//录音状态
									//[7]:0,没有录音;1,有录音;
									//[6:1]:保留
									//[0]:0,正在录音;1,暂停录音;
 	u8 *pname=0;
	u8 timecnt=0;					//计时器   
	u32 recsec=0;					//录音时间
 	u8 recagc=4;					//默认增益为4
  while(f_opendir(&recdir,"0:/RECORDER"))//打开录音文件夹
 	{	 
		Show_Str(30,230,240,16,"RECORDER文件夹错误!",16,0);
		delay_ms(200);				  
		LCD_Fill(30,230,240,246,WHITE);		//清除显示	     
		delay_ms(200);				  
		f_mkdir("0:/RECORDER");				//创建该目录   
	} 
  	f_rec=(FIL *)mymalloc(SRAMIN,sizeof(FIL));	//开辟FIL字节的内存区域 
	if(f_rec==NULL)rval=1;	//申请失败
 	wavhead=(__WaveHeader*)mymalloc(SRAMIN,sizeof(__WaveHeader));//开辟__WaveHeader字节的内存区域
	if(wavhead==NULL)rval=1; 
	recbuf=mymalloc(SRAMIN,512); 	
	if(recbuf==NULL)rval=1;	  		   
	pname=mymalloc(SRAMIN,30);					//申请30个字节内存,类似"0:RECORDER/REC00001.wav"
	p=mymalloc(SRAMIN,30);
	if(pname==NULL)rval=1;
 	if(rval==0)									//内存申请OK
	{      
 		recoder_enter_rec_mode(1024*recagc);				
   		while(VS_RD_Reg(SPI_HDAT1)>>8);			//等到buf 较为空闲再开始  
  		recoder_show_time(recsec);				//显示时间
		recoder_show_agc(recagc);				//显示agc
		pname[0]=0;								//pname没有任何文件名		 
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
					if(recagc>15)recagc=15;				//范围限定为0~15.0,自动AGC.其他AGC倍数										 
					recoder_show_agc(recagc);
					VS_WR_Cmd(SPI_AICTRL1,1024*recagc);	//设置增益,0,自动增益.1024相当于1倍,512相当于0.5倍
					break;
			} 
///////////////////////////////////////////////////////////
//读取数据			  
			if(rec_sta==0X80)//已经在录音了
			{
		  		w=VS_RD_Reg(SPI_HDAT1);	
				if((w>=256)&&(w<896))
				{
	 				idx=0;				   	 
		  			while(idx<512) 	//一次读取512字节
					{	 
			 			w=VS_RD_Reg(SPI_HDAT0);				   	    
		 				recbuf[idx++]=w&0XFF;
						recbuf[idx++]=w>>8;
					}	  		 
	 				res=f_write(f_rec,recbuf,512,&bw);//写入文件
					if(res)
					{
						printf("err:%d\r\n",res);
						printf("bw:%d\r\n",bw);
						break;//写入出错.	  
					}
					sectorsize++;//扇区数增加1,约为32ms	 
				}			
			}else//没有开始录音，则检测TPAD按键
			{			
				if(TPAD_Scan(0))//如果触摸按键被按下,且pname不为空
				{
					reciveTemp = record_voice(3);
					/*
					sprintf((char*)p,"go into tpad %d",timecnt);	
					Show_Str(30+34,115,156,12,p,12,0); 			//显示接收到的数据长度
					reciveTemp = get_now_time();
					
					Show_Str(30+34,450,156,12,(u8 *) reciveTemp,12,0); 			//显示接收到的数据长度
					
					*/
					/*
					pname = "0:/RECORDER/1620709208";
					Show_Str(30,230,240,16,"播放:",16,0);		   
					Show_Str(30+40,230,240,16,pname+11,16,0);	//显示当播放的文件名字   
					TcpSendWav(pname);						//播放pname
					LCD_Fill(30,230,240,246,WHITE);				//清除显示,清除之前显示的录音文件名	  
					recoder_enter_rec_mode(1024*recagc);		//重新进入录音模式		
			   		while(VS_RD_Reg(SPI_HDAT1)>>8);				//等到buf 较为空闲再开始  
			  		recoder_show_time(recsec);					//显示时间
					recoder_show_agc(recagc);					//显示agc
					*/
					/*
					reciveTemp = GetWavName();
					sprintf((char*)p,"send name %d",(u8 *)reciveTemp);	
					Show_Str(30+34,500,156,12,p,12,0); 			//显示接收到的数据长度
					*/
					
 				}
				sprintf((char*)p,"go out tpad %d",timecnt);	
				Show_Str(30+34,150,156,12,p,12,0);
				
				delay_ms(5);
				timecnt++;
				if((timecnt%20)==0)LED0=!LED0;//DS0闪烁 
			}
/////////////////////////////////////////////////////////////
 			if(recsec!=(sectorsize*4/125))//录音时间显示
			{	   
				LED0=!LED0;//DS0闪烁 
				recsec=sectorsize*4/125;
				recoder_show_time(recsec);//显示时间
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


























