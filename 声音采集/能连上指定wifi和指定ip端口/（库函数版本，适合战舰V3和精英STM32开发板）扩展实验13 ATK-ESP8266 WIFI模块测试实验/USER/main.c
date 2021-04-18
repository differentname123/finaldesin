#include "sys.h"
#include "delay.h"
#include "usart.h" 
#include "led.h" 		 	 
#include "lcd.h"  
#include "key.h"     
#include "usmart.h" 
#include "malloc.h"
#include "sdio_sdcard.h"  
#include "w25qxx.h"    
#include "ff.h"  
#include "exfuns.h"   
#include "text.h"	
#include "touch.h"		
#include "usart3.h"
#include "common.h" 
 
/************************************************
 ALIENTEK STM32F103开发板 扩展实验5
 ATK-RM04 WIFI模块测试实验 
 技术支持：www.openedv.com
 淘宝店铺：http://eboard.taobao.com 
 关注微信公众平台微信号："正点原子"，免费获取STM32资料。
 广州市星翼电子科技有限公司  
 作者：正点原子 @ALIENTEK
************************************************/
void sencmd(char *cmd)
{
	u3_printf("%s\r\n",cmd);	//发送命令
}
	
void lianjie()
{
//	u8 ipbuf[16]; 	//IP缓存
//	int times = 0;
//	const u8 *ATK_ESP8266_WORKMODE_TBL[3]={"TCP服务器","TCP客户端"," UDP 模式"};	//ATK-ESP8266,4种工作模式
	char *cmd;
//	int t=1;
	u8 *p;
	USART_RX_BUF[0]='b';
	
	cmd=mymalloc(SRAMIN,100);

	p=mymalloc(SRAMIN,64);	
	
	cmd="AT+UART=115200,8,1,0,0\r\n";
	//sprintf((char*)cmd,"AT+CWJAP=\"%s\",\"%s\"",wifista_ssid,wifista_password);//设置无线参数:ssid,密码
	sencmd(cmd);
	LCD_ShowString(0,0, 240, 16, 16, cmd);
	delay_ms(1000);
	
	
	
	cmd="AT+CWMODE=1\r\n";//sta mode
sencmd(cmd);
	LCD_ShowString(0,200, 240, 16, 16, cmd);
	delay_ms(1000);
	
	cmd="AT+RST\r\n";//restart
sencmd(cmd);
	LCD_ShowString(0,180, 240, 16, 16, cmd);
	delay_ms(1000);
	
	cmd="AT+CWJAP=\"zxh\",\"123456789\"\r\n";//ap information
sencmd(cmd);
	LCD_ShowString(0,160, 240, 16, 16, cmd);
	delay_ms(5000);
	
	cmd="AT+CIPMUX=0\r\n";
sencmd(cmd);
	LCD_ShowString(0,140, 240, 16, 16, cmd);
	delay_ms(5000);
	
		cmd="AT+CIPMODE=0\r\n";
		sencmd(cmd);
		LCD_ShowString(0,100, 240, 16, 16, cmd);
		delay_ms(1000);
		
				sprintf((char*)p,"AT+CIPSTART=\"TCP\",\"%s\",%s","192.168.177.115","12345");   
			while(atk_8266_send_cmd(p,"OK",200))
			{
					LCD_Clear(WHITE);
					POINT_COLOR=RED;
					Show_Str_Mid(0,40,"WK_UP:返回重选",16,240);
					Show_Str_Mid(0,80,"ATK-ESP 连接TCP Server失败",12,240); //连接失败	
			}	
			atk_8266_send_cmd("AT+CIPMODE=1","OK",200);      //传输模式为：透传		
		
}
void senddata(char *data)
{
	int i=0;
	char *cmd;
	cmd=mymalloc(SRAMIN,32);
	while(data[i]!='\0'&&data[i]!='\r'&&data[i]!='\n')
	{
		i++;
	}
	
	
	sprintf((char*)cmd,"AT+CIPSEND=%d\r\n",i);
	//cmd="AT+CIPSEND=4";//开始透传 
	sencmd(cmd);
	//LCD_ShowString(0,80, 240, 16, 16, cmd);
	sencmd(data);
	//LCD_ShowString(0,60, 240, 16, 16, data);
	//delay_ms(1000);
	
}
 int main(void)
 {	 
	u8 key,fontok=0; 	    
	u8 *p;
	delay_init();	    	 //延时函数初始化	  
  NVIC_PriorityGroupConfig(NVIC_PriorityGroup_2);//设置中断优先级分组为组2：2位抢占优先级，2位响应优先级
	uart_init(115200);	 	//串口初始化为115200
 	usmart_dev.init(72);		//初始化USMART		
 	LED_Init();		  			//初始化与LED连接的硬件接口
	KEY_Init();					//初始化按键
	LCD_Init();			   		//初始化LCD   
	W25QXX_Init();				//初始化W25Q128
	tp_dev.init();				//初始化触摸屏
	usart3_init(115200);		//初始化串口3 
 	my_mem_init(SRAMIN);		//初始化内部内存池
	exfuns_init();				//为fatfs相关变量申请内存  
 	f_mount(fs[0],"0:",1); 		//挂载SD卡 
 	f_mount(fs[1],"1:",1); 		//挂载FLASH.
	key=KEY_Scan(0);  
	if(key==KEY0_PRES&&((tp_dev.touchtype&0X80)==0))//强制校准
	{
		LCD_Clear(WHITE);		//清屏0
		TP_Adjust();  			//屏幕校准 
		TP_Save_Adjdata();	  
		LCD_Clear(WHITE);		//清屏
	}
	fontok=font_init();			//检查字库是否OK
	if(fontok||key==KEY1_PRES)	//需要更新字库				 
	{
		LCD_Clear(WHITE);		//清屏
 		POINT_COLOR=RED;		//设置字体为红色	   	   	  
		LCD_ShowString(60,50,200,16,16,"ALIENTEK STM32");
		while(SD_Init())		//检测SD卡
		{
			LCD_ShowString(60,70,200,16,16,"SD Card Failed!");
			delay_ms(200);
			LCD_Fill(60,70,200+60,70+16,WHITE);
			delay_ms(200);		    
		}								 						    
		LCD_ShowString(60,70,200,16,16,"SD Card OK");
		LCD_ShowString(60,90,200,16,16,"Font Updating...");
		key=update_font(20,110,16,"0:");//从SD卡更新
		while(key)//更新失败		
		{			 		  
			LCD_ShowString(60,110,200,16,16,"Font Update Failed!");
			delay_ms(200);
			LCD_Fill(20,110,200+20,110+16,WHITE);
			delay_ms(200);		       
		} 		  
		LCD_ShowString(60,110,200,16,16,"Font Update Success!");
		delay_ms(1500);	
		LCD_Clear(WHITE);//清屏	       
	}  
	//while(1)
	//lianjie();
	//atk_8266_test();		//进入ATK_ESP8266测试
	qiuqiuni("zxh", "12345678", "192.168.177.115", "8086");
		
	//zxh_sta_8266_test();
}


















