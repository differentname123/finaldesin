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
 ALIENTEK STM32F103������ ��չʵ��5
 ATK-RM04 WIFIģ�����ʵ�� 
 ����֧�֣�www.openedv.com
 �Ա����̣�http://eboard.taobao.com 
 ��ע΢�Ź���ƽ̨΢�źţ�"����ԭ��"����ѻ�ȡSTM32���ϡ�
 ������������ӿƼ����޹�˾  
 ���ߣ�����ԭ�� @ALIENTEK
************************************************/
void sencmd(char *cmd)
{
	u3_printf("%s\r\n",cmd);	//��������
}
	
void lianjie()
{
//	u8 ipbuf[16]; 	//IP����
//	int times = 0;
//	const u8 *ATK_ESP8266_WORKMODE_TBL[3]={"TCP������","TCP�ͻ���"," UDP ģʽ"};	//ATK-ESP8266,4�ֹ���ģʽ
	char *cmd;
//	int t=1;
	u8 *p;
	USART_RX_BUF[0]='b';
	
	cmd=mymalloc(SRAMIN,100);

	p=mymalloc(SRAMIN,64);	
	
	cmd="AT+UART=115200,8,1,0,0\r\n";
	//sprintf((char*)cmd,"AT+CWJAP=\"%s\",\"%s\"",wifista_ssid,wifista_password);//�������߲���:ssid,����
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
					Show_Str_Mid(0,40,"WK_UP:������ѡ",16,240);
					Show_Str_Mid(0,80,"ATK-ESP ����TCP Serverʧ��",12,240); //����ʧ��	
			}	
			atk_8266_send_cmd("AT+CIPMODE=1","OK",200);      //����ģʽΪ��͸��		
		
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
	//cmd="AT+CIPSEND=4";//��ʼ͸�� 
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
	delay_init();	    	 //��ʱ������ʼ��	  
  NVIC_PriorityGroupConfig(NVIC_PriorityGroup_2);//�����ж����ȼ�����Ϊ��2��2λ��ռ���ȼ���2λ��Ӧ���ȼ�
	uart_init(115200);	 	//���ڳ�ʼ��Ϊ115200
 	usmart_dev.init(72);		//��ʼ��USMART		
 	LED_Init();		  			//��ʼ����LED���ӵ�Ӳ���ӿ�
	KEY_Init();					//��ʼ������
	LCD_Init();			   		//��ʼ��LCD   
	W25QXX_Init();				//��ʼ��W25Q128
	tp_dev.init();				//��ʼ��������
	usart3_init(115200);		//��ʼ������3 
 	my_mem_init(SRAMIN);		//��ʼ���ڲ��ڴ��
	exfuns_init();				//Ϊfatfs��ر��������ڴ�  
 	f_mount(fs[0],"0:",1); 		//����SD�� 
 	f_mount(fs[1],"1:",1); 		//����FLASH.
	key=KEY_Scan(0);  
	if(key==KEY0_PRES&&((tp_dev.touchtype&0X80)==0))//ǿ��У׼
	{
		LCD_Clear(WHITE);		//����0
		TP_Adjust();  			//��ĻУ׼ 
		TP_Save_Adjdata();	  
		LCD_Clear(WHITE);		//����
	}
	fontok=font_init();			//����ֿ��Ƿ�OK
	if(fontok||key==KEY1_PRES)	//��Ҫ�����ֿ�				 
	{
		LCD_Clear(WHITE);		//����
 		POINT_COLOR=RED;		//��������Ϊ��ɫ	   	   	  
		LCD_ShowString(60,50,200,16,16,"ALIENTEK STM32");
		while(SD_Init())		//���SD��
		{
			LCD_ShowString(60,70,200,16,16,"SD Card Failed!");
			delay_ms(200);
			LCD_Fill(60,70,200+60,70+16,WHITE);
			delay_ms(200);		    
		}								 						    
		LCD_ShowString(60,70,200,16,16,"SD Card OK");
		LCD_ShowString(60,90,200,16,16,"Font Updating...");
		key=update_font(20,110,16,"0:");//��SD������
		while(key)//����ʧ��		
		{			 		  
			LCD_ShowString(60,110,200,16,16,"Font Update Failed!");
			delay_ms(200);
			LCD_Fill(20,110,200+20,110+16,WHITE);
			delay_ms(200);		       
		} 		  
		LCD_ShowString(60,110,200,16,16,"Font Update Success!");
		delay_ms(1500);	
		LCD_Clear(WHITE);//����	       
	}  
	//while(1)
	//lianjie();
	//atk_8266_test();		//����ATK_ESP8266����
	qiuqiuni("zxh", "12345678", "192.168.177.115", "8086");
		
	//zxh_sta_8266_test();
}


















