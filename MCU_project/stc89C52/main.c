/******************************************

���ܣ�����AD0832��ȡģ���ѹ��ֵ����ADת��
	  �����ֵ��ʹ�ô��������ADC0832��CHO
	  ��ͨ��0���Ǽ���ѹ�仯��
	  ʵ�ִ�������������ǵ�Ƭ�����������
	  �������Ҫ�Ĺ��ߡ���ʹ��ǰ��Ҫ��Ҫ
	  �����嵱ǰ����Ƶ���Ƿ�Ϊ11.0592MHz,��
	  ���ǣ���������񣬻������м��㶨ʱ��
	  ��ֵ��
MCU:STC89C52
����������KeilC

******************************************/


//ͷ�ļ�
#include <reg51.h>
#include <INTRINS.H>

//�궨��
#define uchar unsigned char 
#define uint unsigned int 

//�ܽŶ���
sbit	ADC0832_CLK = P3^5;
sbit	ADC0832_DODI =P2^7;
sbit	ADC0832_CS = P3^4;

//��������
/******************************************/					
void delay(unsigned int time); 


/******************************************/
//��ʱ�Ӻ��� 
//������Χ 0-65536
void delay(unsigned int time)   //���� time ��С
{							     //������ʱʱ�䳤��

	while(time--);
}

//------------------------------------------------ADC0832��ȡ����------------------------------------------------------
/************************************************************/
//��ȡADC0832ת��ֵ����
//��ں�����channelѡ��ͨ��
uchar ADC0832_Read(uchar channel)
{
	uchar i = 0;	
	uchar tmp = 0;	//���ֽ�����
	uchar tmp1 = 0;	//���ֽ�����
	
	//��ʼ��־λ
	ADC0832_DODI = 1;
	_nop_();  _nop_();

	ADC0832_CS = 0;		//Ƭѡ�ź�	

	ADC0832_CLK = 1;
	_nop_();  _nop_();
	ADC0832_CLK = 0;
	_nop_();  _nop_();

	//ѡ��ͨ��
	switch(channel)
	{
		case 0:		//ͨ��0
			ADC0832_DODI = 1;	//SGL = 1��
			_nop_();  _nop_();
			ADC0832_CLK = 1;
			_nop_();  _nop_();
			ADC0832_CLK = 0;
			_nop_();  _nop_();

			ADC0832_DODI = 0;	//ODD = 0;
			_nop_();  _nop_();
 			ADC0832_CLK = 1;
 			_nop_();  _nop_();
			ADC0832_CLK = 0;
			_nop_();  _nop_();
			break;

		case 1:		//ͨ��1
			ADC0832_DODI = 1;	//SGL = 1��
			_nop_();  _nop_();
			ADC0832_CLK = 1;
			_nop_();  _nop_();
			ADC0832_CLK = 0;
			_nop_();  _nop_();

			ADC0832_DODI = 1;	//ODD = 1;
			_nop_();  _nop_();
 			ADC0832_CLK = 1;
 			_nop_();  _nop_();
			ADC0832_CLK = 0;
			_nop_();  _nop_();
			break;
			
		default:
			break;		
	}

	ADC0832_DODI = 1;
	_nop_();  _nop_();

	//��ȡ8λת����ֵ
	for(i=0;i<8;i++)
	{
		ADC0832_CLK = 1;	   //����ͬ��ʱ��
		_nop_();  _nop_();
		ADC0832_CLK = 0;
		_nop_();  _nop_();

		if(ADC0832_DODI)	  //��λ��ȡ8λ����
		{
			tmp |= 0x01;	  //�ӵ�λ��ȡ
		}
		else
		{
			tmp &= 0xfe;
		}
		if(i<7)
		{
			tmp <<= 1;		  //��λ
		}					
	}

	tmp1 =  tmp & 0x01;
	tmp1 <<= 6;

	for(i=0;i<7;i++)		  //��ȡ7λУ������
	{
		ADC0832_CLK = 1;
		_nop_();  _nop_();
		ADC0832_CLK = 0;
		_nop_();  _nop_();

		if(ADC0832_DODI)
		{
			tmp1 |= 0x80;
		}
		else
		{
			tmp1 &= 0x7f;
		}
		if(i<6)
		{
			tmp1 >>= 1;
		}		
	}

	ADC0832_CS = 1;		//��ֹADC0832

	if(tmp == tmp1)	//�����ֽ�����ֽ���ͬʱ����ֵ��Ч
	{
		return tmp;	//������ֵ
	}
}


//------------------------------------------------���ڳ�ʼ��------------------------------------------------------
void Uart_Init( void )
{  							//������9600
	TMOD = 0x20;	//��ʱ��T1ʹ�ù�����ʽ2
	TL1 = 0xfa;
	TH1 = 0xfa;
	TR1 = 1;    	// ��ʼ��ʱ
	SCON = 0x50;	//������ʽ1��������9600bps @ 11.0592���������
	PCON |= 0x80;
	//ES = 1;
	//EA = 1;    		// �������ж�
	TI = 0;
	//RI = 0;
	//REN = 1;
}
//------------------------------------------------������ʾ----------------------------------------------------------
void RS_Byte(uchar R_Byte)
{	
	 SBUF = R_Byte;  
     while(TI == 0);				//��ѯ��
  	 TI = 0;
     
}

void RS_String(uchar *p)
{
	uchar i = 0;

	while(*(p+i) != '\0' )
	{
		RS_Byte(*(p+i));
		i++;	
	}
}
					
/******************************************/
//������				
void main(void) 
{
	uchar AD_Value;	

	Uart_Init();

	RS_String("Uart Initializing...... \n");

	delay(50000);

	RS_String("Uart Initializing Finished !! \n");

	delay(50000);

	while(1)			//������ѭ�� 	
	{
		AD_Value = ADC0832_Read(0);

		RS_String("The value of AD is : ");
		RS_Byte(AD_Value/100 + '0');
		RS_Byte(AD_Value/10%10 + '0');
		RS_Byte(AD_Value%10 + '0');
		RS_Byte('\n');

		delay(50000);
	}
}

