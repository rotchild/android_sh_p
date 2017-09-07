/*! @proto.h
********************************************************************************
ģ����       : StreamServer ControlServer ArchiveServer FileServer 
               ServersManager ...
�ļ���       : proto.h
����ļ�     : 
�ļ�ʵ�ֹ��� : �����˸�ģ��֮��ͨѶ��Э�����غ궨��
				�����ֶζ���ѭ�����ʽ
����         : ybfan@wtwh.com.cn, xpchen@wtwh.com.cn
�汾         : 5.0.0
ʱ��         : 2005/07/07
--------------------------------------------------------------------------------
��ע         : <����˵��>
--------------------------------------------------------------------------------
�޸ļ�¼ : 
����			�汾		�޸���					�޸�����
2005/07/07		5.0.0	ybfan@wtwh.com.cn		1.IpcReport�������û�����		
						xpchen@wtwh.com.cn		2.AlertNotifyFormat��EventActFormat
												3.QuestReject����CameraSign
												4.������״̬������޸�
				5.0.1	ybfan@wtwh.com.cn		1.���ӿͻ��������Ͽ����ӵ�����ͺꡣ
				5.0.2	xpchen@wtwh.com.cn		1.���ӿͻ�������Ʒ���֤�����ĺ�
2005/07/11		5.0.3	ybfan@wtwh.com.cn		1.���״̬���������
												2.��ӱ�������������
2005/07/11		5.0.4	ybfan@wtwh.com.cn		1.�ϲ��������������ݶ˿��޸�������Э��
                5.0.5   xpchen@wtwh.com.cn		1.���ñ���������ֹ���ʼ/ֹͣ�洢�ṹ
2005/07/14		5.0.6	xpchen@wtwh.com.cn		1.��ӿͻ�������Ʒ����ˢ������
2005/07/16		5.0.7	xpchen@wtwh.com.cn		1.�޸� StreamEncoderStat FileServCameraList
												  ���ӵ�ǰ������Чʵ������
												2.�޸� EventActFormat 
2005/07/17		5.0.8	ybfan@wtwh.com.cn		1.�޸�liveRequest ��������
2005/07/21		5.0.9	ybfan@wtwh.com.cn		1.ȥ������������,�޸��ļ���ʽ

*******************************************************************************/
#ifndef PROTO_H
#define PROTO_H

#include <stdlib.h>

#define _MAX_PATH 255
//���Ͷ���
typedef unsigned long				DWORD;
typedef int							BOOL;
typedef unsigned char				BYTE;
typedef unsigned short				WORD;
typedef unsigned int				UINT;

#define FUNCTIONFAIL				-1
#define CHECKTIME					5

#define ARCHIVE_INFO_SIZE			50
#define NAMEPWD_SIZE				32

//���ݰ���С
//#define PAYLOADSIZE				532

#define SPOOLSIZE					256
#define MAXPAYLOADDATASIZE			2048

#define MAXREQUESTHOPCOUNT			8	//��ྭ���������Ŀ

#define UDPCLIENT					1
#define TCPCLIENT					2


//�������������궨��
#define VT_NONE						0x00
#define VT_WTMP4					0x01
#define VT_H263						0x02
#define VT_H264						0x03

#define AT_NONE						0x40
#define AT_MP2						0x41
#define AT_ADPCM					0x42
#define AT_UPCM						0x43

#define ST_STATUS					0xFF

//�ܾ�ԭ��
#define	ILLEGALUSER					0x0002		//�Ƿ��û�
#define	STREAMNOTFOUND				0x0101		//δ����ָ����
#define	FILENOTFOUND				0x0201		//δ����ָ���ļ�
#define	ERRORABNORMITY				0xFFFF		//δָ�����쳣

//Errono
#define ENCORDERNOTEXIST			0x0001		//������������
#define ENCORDERDUPLICATE			0x0002		//����������

//EncoderType
#define HARDENCORDER				0x01		//ʵ�ʱ�����
#define PROXYENCORDER				0x02		//ͨ������ʽ�õ��ı�����


//����汾
#define COMMANDVERSION01			0x01

//�ļ����������
#define COMMAND_FILEREQUEST			0x10
#define COMMAND_FILEDESC 			0x11		//�ļ�����
#define COMMAND_FILEDOWNLOAD		0x12

#define COMMAND_NONE				0xFF		//�ղ��������ı��κ�״̬�������Сֻ����4���ֽ�	
#define COMMAND_REJECTSERVICE		0xEE		//�ܾ�����

#define COMMAND_LIVESTREAMDESC		0x21		//livestream����
#define COMMAND_LIVEQUEST			0X22		//����live
#define COMMAND_DOWNLOADLIVE		0X23		//����live
#define COMMAND_CLOSECONNECT		0X28		//�Ͽ�����


// ����Ipcamera�����еĶ���һ��
#define COMMAND_ALERTREPORT			0x00		//�����������ͻ��˵ģ���������
#define COMMAND_CLIENTLIVE			0x03		//���ͻ��˸��������ģ�����
#define COMMAND_ALERTPROCESS		0x06		//���ͻ��˸��������ģ���������
#define COMMAND_RECORDPROCESS		0x07		//���ͻ��˸��������ģ�¼����
#define COMMAND_CAMERALIST			0x08		//���ͻ��˸��������ģ�������״̬�б�
#define COMMAND_FREEDISKLIST		0x09		//���ͻ��˸��������ģ��ļ�������̿ռ��б�


#define COMMAND_TIME				0x30		//�����ʱ
#define COMMAND_PROXYREQUEST		0x34		//����������
#define COMMAND_PROXYRESPONSE		0x35		//���������


//�洢���������   0xA0 ~ 0xBF
#define COMMAND_REQ_ASSIGNIPC		0xA0
#define COMMAND_REQ_REMOVEIPC		0xA1
#define COMMAND_REQ_ARCHIVE			0xA4
#define COMMAND_REQ_CHGARCHIVE		0xA5
#define COMMAND_RESP_ARCHIVE		0xA6
#define COMMAND_RESP_FILEEND		0xAB
#define COMMAND_RESP_POLICYEND		0xAC

#define COMMAND_REQ_IPCLOST			0xB0
#define COMMAND_REQ_HANDSTOP		0xB1
#define COMMAND_REQ_DELFILE			0xB4
#define COMMAND_RESP_DELFILE		0xB5
#define COMMAND_RESP_DELRECORD		0xB6
#define COMMAND_RE_UNKNOWN			0xBF

//���������������   0xC0 ~
#define COMMAND_STREAM_STAT			0xC0
#define COMMAND_STREAM_CONFIG		0xC1
#define COMMAND_STREAM_CAMERA		0xC2	//�õ�streamServer �� camear�б�

#define COMMAND_FILE_IN_STAT		0xD1
#define COMMAND_FILE_OUT_STAT		0xD2
#define COMMAND_FILE_CONFIG			0xD3
#define COMMAND_FILE_DISK			0xD4
#define COMMAND_FILE_CAMERA			0xD5

#define COMMAND_CONTROL_QUESTLIST	0xD6		//�������ͻ�������Ʒ�������������б�


#define COMMAND_ENCODER_CONTROL		0xE1		//��������������
#define COMMAND_ENCODER_DATA		0xE2		//����������
#define COMMAND_ENCODER_DESC		0xE3		//����������




//ͨ������ͷ�ṹ
typedef	struct tagCommonCommandHead{
	BYTE 	bCommandVersion;					//��������汾,��ǰΪ0x01
	BYTE 	bCommandType;						//������������,
	WORD	wCommandLen;						//�����С
}CommandHead, *pCommandHead;






////////////////////////////////////////////////////////////////////////
//
// �������������������ͣ��������������ݣ�
//
//Media��Ϣ������ý����������������������ṹ��
typedef struct tagMediaDesc
{
	BYTE 	bMediaID;							//ý���ʶ 0~31
	BYTE 	bPayload;							//ý�帺������
	BYTE 	bQualityClass;						//�����ȼ� 0~7,0������Ҫ����Ҫ�����������Ӷ�����
	BYTE 	bBitrate;							//��λ8Kbps=1kBYTE/s 0:��ʾ�ɱ�����
	char	pszMediaSpcialInfo[8];				//ý�������������ݲ�ͬbPayloadtype��ͬ
}MediaDesc, *pMediaDesc;

//����ý������
//wt_mp4,mp4,263��
typedef struct tagVideoMediaInfo
{
	BYTE 	bWidth;								//�����ȵ�λ8pixel
	BYTE 	bHeight;							//����߶ȵ�λ8pixel	
	BYTE 	bFrameRate;							//֡��
	BYTE 	bExtend;							//��չ��־ 1������չ
	WORD	wOffset;							//���ƫ����
	WORD	wSize;								//��С
}VideoMediaInfo, *pVideoMediaInfo;

//mp2,adpcm��
typedef struct tagAudioMediaInfo
{
	DWORD	dwSamplepersecond;					//����Ƶ��
	WORD	wChannel;							//ͨ����
	WORD	wBitsPerSample;						//����λ��
} AudioMediaInfo, *pAudioMediaInfo;

//Stream�������������ŵ��� bMediaNumber �� MediaDesc
typedef struct tagStreamDesc
{
	CommandHead	dwCommandHead;					//�������ͣ�COMMAND_LIVESTREAMDESC
	DWORD	dwCameraSign;						//Ipcamera��ʶ
	WORD	wClientID;							//�������������������ͻ���ʱ����λ�����clinetID
	BYTE 	bMediaNumber;						//������������Media�� ���32
	BYTE 	bChangeFlag;						//���øĶ���ʶ ��0��ʼÿ�η����Ķ���1
	//MediaDesc	pstMediaDesc[32];				//���������ɸ�������
}StreamDesc, *pStreamDesc;

// ������ͷ��ʽ��ռ16���ֽڣ����� payload ռ512�ֽ�
//
//	0                   1                   2                   3
//	 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
//	+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
//  |    Version    |   Cmd Type    |     Command Length            | Header
//	|  ChgFlag  |      FrameSeq     |  FC | MediaID |    Reserved   | FC:FrameClass
//	|   Packet Seq  |    PackNum    |          Sequence Number      | Header
//	|                           CameraSign                          |
//	|                            timestamp                          | Header
//	+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
//	|                                                               |
//	|       payload	                                                | 
//	|                                                               | 
//	|                               		                        |
//	|                                                               |
//	+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
typedef struct tagPayloadFormatHead				//���ظ�ʽ
{
	CommandHead	dwCommandHead;			 		//�������ͣ�COMMAND_ENCODER_DATA
	union
	{
		struct
		{
			DWORD bitReserved	: 8;			//�����ֶ�
			DWORD bitMediaID	: 5;			//ý��ID����stream����һMedia�ı��
			DWORD bitFrameClass	: 3;			//֡�����͡���Ƶ֡ʱ��0 -> I֡��1 -> P֡��
												//			��ƵʱΪ0
			DWORD bitFrameSeq	: 10;			//��ǰ֡�����кţ�ͬһMedia��֡���к����μ�1
												//��Media֮������к�û�й�ϵ
			DWORD bitChangeFlag : 6;			//�������ĸı��ʶ����StreamDesc�е�bChangeFlag��Ӧ


		};
		DWORD dwFirstDWORD;							//ͷ˫��
	};
	BYTE	bPacketIndex;						//����š��˰��ڴ�֡�е����кţ���0��ʼ��
	BYTE	bPackNum;							//����������֡���зְ��ĸ�����
	WORD	wSequenceNum;						//���������˰������а��е����кš���Media֮���޹�ϵ��
	DWORD	dwCameraSign;						//�����ʶ��
	DWORD	dwTimestamp;						//ʱ�������֡��Ӧ�Ĳ���ʱ�����
}PayloadFormatHead, *pPayloadFormatHead;
//
//
//
////////////////////////////////////////////////////////////////////////






////////////////////////////////////////////////////////////////////////
//
// �û��������������������������
//

//�û���������������������
typedef struct tagLiveStreamRequest
{
	CommandHead	dwCommandHead;
	DWORD		dwCameraSign;
	BYTE		bRequestHopCount;
	BYTE		bReserve;
	WORD		wReserved;
	char		pszName[NAMEPWD_SIZE];
	char		pszPassWord[NAMEPWD_SIZE];
}LiveStreamRequest,*pLiveStreamRequest;

//�ܾ�Ӧ������ĸ�ʽ��**��Ҫ�����Ƿ���� Sign ��Ϣ**
typedef struct tagQuestReject
{
	CommandHead dwCommandHead;					//��������:COMMAND_REJECTSERVICE
	DWORD		dwCameraSign;					//�ܾ���Ipcamera��ʶ
	WORD		wRejectReason;					//�ܾ�ԭ��
	WORD		wReserve;
}QuestReject, *pQuestReject;

//�û�����������������˿ڷ������������������صĸ�ʽ
//����ý��ĵȼ�
typedef struct tagMediaDownloadDesc
{
	BYTE		bRequestMediaID;				//Ҫ���mediaid
	BYTE		bRequestMediaClass;				//Ҫ���media�ȼ�
}MediaDownloadDesc;

//�������صĸ�ʽ
typedef struct tagStreamDataDownload
{
	CommandHead	dwCommandHead;					//��������:COMMAND_DOWNLOADLIVE
	DWORD		dwCameraSign;					//Ipcamera��ʶ
	WORD		wClientID;						//������ͻ���ID
	BYTE		bRequestMediaNum;				//Ҫ���media����
	BYTE		bReserve;						//����
	MediaDownloadDesc mediaDownloadDesc[32];
}StreamDataDownload, *pStreamDataDownload;	

//�û��Ͽ���������
typedef struct tagCloseConnect
{
	CommandHead dwCommandHead;					//��������:COMMAND_CLOSECONNECT
	DWORD		dwCameraSign;					//Ipcamera��ʶ
	WORD		wClientID;						//����Ŀͻ�ID
	BYTE		bReserve;						//����
	BYTE		bReserve2;						//����
}CloseConnect, *pCloseConnect;
//
//
//
////////////////////////////////////////////////////////////////////////






////////////////////////////////////////////////////////////////////////
//
// ���������ʱ����
//
// ���������ʱ���������󣬶�ʱ�������������Ӧʱ��
typedef struct tagCorrectTime
{
	CommandHead dwCommandHead;						//��������:COMMAND_TIME
	DWORD		dwTime;								//��1970.1.1�յ����ڵ�������������ʱ��
}CorrectTime, *pCorrectTime;
//
//
//
////////////////////////////////////////////////////////////////////////






////////////////////////////////////////////////////////////////////////
//
// �ͻ�����������
//
// �ͻ�����������������
typedef struct tagProxyQuest
{
	CommandHead dwCommandHead;						//
	char		pszName[NAMEPWD_SIZE];				//���������û���
	char		pszPassword[NAMEPWD_SIZE];			//������������
}ProxyQuest, *pProxyQuest;

//���������ͻ��˻�Ӧ�����������ַ
typedef struct tagProxyAddr
{
	CommandHead dwCommandHead;
	char		pszAddr[20];						//��������ַ
}ProxyAddr, *pProxyAddr;
//
//
//
////////////////////////////////////////////////////////////////////////






////////////////////////////////////////////////////////////////////////
//
// ������������������������Ʒ��񡢿��Ʒ�����ͻ��ˡ���������ͻ���
//
// ����������Ʒ����͵�״̬����/��������/��̨��������ʾ�ο��ĵ���Ipcamera���á�
//
// ���У��ͻ�������Ʒ���ͨѶ�Ľṹ�У�tagIpcReport��ǰ�滹����CommandHeadͷ��
// CommandHeadͷ�е�wCommandLenΪsizeof(tagIpcReport)��
// �������ͨѶ�ĸ��ṹ�о�û��CommandHeadͷ��
//
//
//
//
//
// 0               1               2               3               4            
// 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2
// +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
// |                          CameraSign                           | 
// +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
// |       sequence number         |       Control word size       | 
// +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
// | Control word 1| Control word 2|      ...      |       ...     |
// +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
#define CONTROL_ADPCM_AUDIO		0xD0
typedef struct tagIpcReport
{
	DWORD	dwCameraSign;							//Ipcamera��ʶ
	WORD	wSequenceNumber;						//�������к�
	WORD	wReportSize;							//�����С�������� tagIpcReport ��ǰ����
	BYTE	bReportType;							//��������
	BYTE	bUserClass;								//�û�����
	BYTE	bReportContent;							//��������1
	BYTE	bReportContent2;						//��������2
	char	pszReportInfo[20];						//��������
} IpcReport, *pIpcReport;
//
//  �����������Ҫ����		״̬����		����					��̨����	
//	WORD ReportSize;		0x03		0x04(+20)			0x04	
//	BYTE ReportType;		0x00		0x00				0x02
//  BYTE UserClass;			0x00		0x00				�û��ȼ�
//	BYTE ReportContent;		0x00		0x66(��)0x65(��)		��̨��������������...��				
//	BYTE ReportContent2;				�˿ں�				��̨�ٶ�
//
//
//
//
////////////////////////////////////////////////////////////////////////






////////////////////////////////////////////////////////////////////////
//
// �ͻ������ļ���������
//
//�û�����������ļ������˿ڷ������¸�ʽ����������
typedef struct tagFileRequest
{
	CommandHead	dwCommandHead;					//��������:COMMAND_FILEREQUEST
	char		pszName[NAMEPWD_SIZE];			//�û���
	char		pszPassword[NAMEPWD_SIZE];		//����
	char		pszFileName[256];				//�ļ���������·����
}FileRequest, *pFileRequest;

//���������û������ļ�����Ϣ:
typedef struct tagFileDesc
{
	CommandHead dwCommandHead;					//��������:COMMAND_FILEDESC
	DWORD		dwSizeofHead;					//�ļ�ͷ��С
	DWORD		dwSizeofFile;					//�ļ���С
}FileDesc, *pFileDesc;

//�û�����������ļ������˿ڷ������¸�ʽ���������ļ�
typedef struct tagFileDownload
{
	CommandHead	dwCommandHead;					//��������:COMMAND_FILEDOWNLOAD
	DWORD		dwBeginOffset;					//������ļ���ʼλ��
	DWORD		dwDownloadSize;					//������ֽ���
}FileDownload, *pFileDownload;

// �ļ�ͷ��ʽ
typedef struct tagFileMediaDesc
{
	DWORD		dwCameraSign;					//Ipcamrea��ʶ
	BYTE		bMediaNum;
	BYTE		bReserve[3];
	MediaDesc	mMediainfo[8];
}FileMediaDesc,*pFileMediaDesc;

typedef struct tagFileHead
{
	WORD		wVersion;							//�汾��				
	WORD		wHeadSize;							//�ļ�ͷ�Ĵ�С
	DWORD		dwFileSize;							//�ļ���С���ļ�δ��������䣩
	DWORD		dwPresentionTime;					//����ʱ��
	BYTE		bStreamNum;							//һ�����м�·��
	BYTE		bReserve[3];
	FileMediaDesc	fFileDesc[8];
}FileHead, *pFileHead;
//
//
////////////////////////////////////////////////////////////////////////






////////////////////////////////////////////////////////////////////////
//
// ���Ʒ������ļ��洢����
// �������������е�����(Requst, Req)����ָ���Ʒ�����洢���������
// ���е�Ӧ��(Response, Resp)����ָ�洢��������Ʒ����Ӧ��
//
//��ĳһ· sign ������洢���������
typedef struct tagAssignIpcReq
{
	CommandHead dwCommandHead;					//
	DWORD		dwCameraSign;					//Ipcamera��ʶ
	char		pszCenterServerIP[20];			//�� Ipcamera ���ڵ�����������ַ
	WORD		wCenterServerPort;				//���������˿�
}AssignIpcReq, *pAssignIpcReq;

//��ĳһ· sign �Ӵ洢������������
typedef struct tagRemoveIpcReq
{
	CommandHead dwCommandHead;
	DWORD		dwCameraSign;					//Ipcamera��ʶ
}RemoveIpcReq, *pRemoveIpcReq;

//��ĳһ· sign Ҫ��ʼ�洢������
typedef struct tagArchiveReq
{
	CommandHead dwCommandHead;
	DWORD		dwPolicyID;						//�洢����ID
	DWORD		dwCameraSign;					//Ipcamera��ʶ
	DWORD		dwEventStartTime;				//�洢��ʼʱ��
	DWORD		dwEventStopTime;				//�洢����ʱ��
}ArchiveReq, *pArchiveReq;

//ĳһ· sign ��ʼ�洢���ݵ�Ӧ��
typedef struct tagArchiveResp
{
	CommandHead dwCommandHead;					
	DWORD		dwPolicyID;						//�洢����ID
	DWORD		dwCameraSign;					//Ipcamera��ʶ
	char		pszFileName[_MAX_PATH];			//�洢�ļ���
}ArchiveResp, *pArchiveResp;

//ĳһ· sign �ı�洢���Ե�����
typedef struct tagChgArchReq
{
	CommandHead dwCommandHead;
	DWORD		dwPolicyID;						//�洢����ID
	DWORD		dwCameraSign;					//Ipcamera��ʶ
	DWORD		dwEventStopTime;				//�洢����ʱ��
}ChgArchReq, *pChgArchReq;


//ĳһ���洢������ɵ�Ӧ��
typedef struct tagPolicyEndResp
{
	CommandHead dwCommandHead;
	DWORD		dwPolicyID;						//�洢����ID
	DWORD		dwCameraSign;					//Ipcamera��ʶ 
}PolicyEndResp, *pPolicyEndResp;

//ĳһ�����������ߵ�����
typedef struct tagIpcLostReq
{
	CommandHead dwCommandHead;
	DWORD		dwCameraSign;					//Ipcamera��ʶ
}IpcLostReq, *pIpcLostReq;

//�ֶ�ֹͣ�洢������
typedef struct tagHandStopReq
{
	CommandHead dwCommandHead;
	DWORD		dwPolicyID;						//�洢����ID
	DWORD		dwCameraSign;					//Ipcamera��ʶ
}HandStopReq, *pHandStopReq;

//ɾ���ļ�������
typedef struct tagDelFileReq
{
	CommandHead dwCommandHead;
	char		pszFileName[_MAX_PATH];			//����ɾ�����ļ���
}DelFileReq, *pDelFileReq;

//ɾ���ļ���Ӧ��
typedef struct tagDelFileResp
{
	CommandHead dwCommandHead;
	char		pszFileName[_MAX_PATH];			//����ɾ�����ļ���
	BYTE		bFinished;						//ɾ���ɹ�����ʧ�ܣ��ɹ�Ϊ1��ʧ��Ϊ0
}DelFileResp, *pDelFileResp;

//ɾ����¼��Ӧ��
typedef struct tagDelRecordResp
{
	CommandHead dwCommandHead;
	char		pszFileName[_MAX_PATH];			//����ɾ������ļ���ص��ļ���¼
}DelRecordResp, *pDelRecordResp;
//
//
////////////////////////////////////////////////////////////////////////






////////////////////////////////////////////////////////////////////////
//
// ������������������/���������
// ��Ҫ״̬����ĳ���������ط���������״̬����ֻ���CommnadHead
// ��������յ���������״̬���淵��


//������/�������������������򱨸��������
//������������������/��������������øı�
typedef struct tagStreamServConfig
{
	CommandHead dwCommandHead;					
	WORD		wSourceStreamPort;				//�������������ݶ˿ں�
	WORD		wQuestStreamPort;				//�û��������ݶ˿ں�
}StreamServConfig, *pStreamServConfig;


//������������������б�
typedef struct tagEncoderList
{
	CommandHead dwCommandHead;					//��������:COMMAND_STREAM_CAMERA
	WORD		wOnlineIpcNum;					//��ǰ���߱���������
	WORD		wStartSeq;						//������ʼλ��
	WORD		wActualIpcNum;					//�����еı���������
	WORD		wReserved;
	DWORD		dwCameraSign[100];				//CameraSign�б� ע����Ϊ�������б���ܴܺ󣬿��ǵ�һ�ο��ܷ��Ͳ��꣬
												//���Լ���wStartseq.ÿ������಻����10 Encorder
	BYTE		bEncorderType[100];				//encoder����
}EncoderList, *pEncoderList;

//
////////////////////////////////////////////////////////////////////////






////////////////////////////////////////////////////////////////////////
//
// �������������ļ�����
//
//�ļ�������������������򱨸�״̬


//�ļ�������������������򱨸��������
//�������������ļ��������������øı�
typedef struct tagFileServConfig
{
	CommandHead dwCommandHead;
	WORD		wListenPort;					//�ļ�������������˿ں�
	WORD		wClientBrokenTime;				//�û�����ʱ��
}FileServConfig, *pFileServConfig;

//������������������б�
typedef struct tagCameraStat 
{
	DWORD		dwCameraSign;
	BYTE		bState;							//�Ƿ�洢
	BYTE		bReason;						//�洢ԭ��
	WORD		wReserve;
}CameraStat, *pCameraStat;

typedef struct tagFileServCameraList
{
	CommandHead dwCommandHead;
	WORD		wOnlineIpcNum;					//��ǰ���߱���������
	WORD		wStartSeq;						//������ʼλ��
	WORD		wActualIpcNum;					//�����еı���������
	WORD		wReserved;
//	CameraState cState[wOnlineIpcNum];			//CameraSign�б� ע����Ϊ�������б���ܴܺ��ǵ�һ�ο��ܷ��Ͳ��꣬
												//���Լ���wStartseq.�Ƽ�udp���Ͳ�Ҫ����1k��С
}FileServCameraList, *pFileServCameraList;

//
//
////////////////////////////////////////////////////////////////////////


////////////////////////////////////////////////////////////////////////
//
// �������ͻ�������Ʒ���
//
//�������ͻ�������Ʒ�������ϵͳ���������ݷ�����ļ������ַ��Ϣ
typedef struct tagServerMeta
{
	char		pszServerIP[20];						// ���ݷ�������ļ������ַ
	WORD		wServerPort;							// ����״̬�����˿ں�
	WORD		wServerType;							// �������ͣ� 0�����ݷ��� 1���ļ�����
}ServerMeta, *pServerMeta;

typedef struct tagServersInfo
{
	CommandHead dwCommandHead;							// ���е� wCommandLen ���� servers[dwServerNum]�Ĵ�С
	DWORD		dwServerNum;							// ����ʵ�������
//	ServerMeta	servers[dwServerNum];
}ServersInfo, *pServersInfo;
//
//
////////////////////////////////////////////////////////////////////////



////////////////////////////////////////////////////////////////////////
//
// �ͻ�������Ʒ���
//
// ֪ͨ���ͻ��˵ı����ṹ
typedef struct tagAlertNotifyFormat
{
	CommandHead dwCommandHead;
	DWORD		dwCameraSign;						//�����ʶ
	WORD		wSequence;							//LT��ţ�ʶ���Ƿ�ͬһ����
	WORD		wAlertPortID;						//�����˿�ID
	DWORD		dwEventTypeID;						//�����¼�����
	DWORD		dwEventLogID;						//������¼ID
	char		pszEventType[20];					//�����¼�����					
}AlertNotifyFormat, *pAlertNotifyFormat;


//�ͻ�������Ʒ�����������ṹ
typedef struct tagClientQuest
{
	CommandHead dwCommandHead;						// ���е� wCommandLen ���� ��������Ĵ�С
	DWORD		dwObjectNum;						// �������������������CameraLiveStat��DiskFreeSize
}ClientQuest, *pClientQuest;

//������״̬�б����
typedef struct tagCameraLiveStat
{
	DWORD		dwCameraSign;						//Ipcamera��ʶ
	BYTE		bOnline;							//�Ƿ�����
	BYTE		bStore;								//�Ƿ����ڴ洢
	BYTE		bReserved;
	BYTE		bReserved2;
}CameraLiveStat, *pCameraLiveStat;

//
//
////////////////////////////////////////////////////////////////////////



////////////////////////////////////////////////////////////////////////
//
// ������/���������ר��
//
typedef struct tagMaxPayload
{
	PayloadFormatHead payloadHead;
	char			  payloadData[MAXPAYLOADDATASIZE];
}MaxPayload;

typedef struct {
	StreamDesc		streamDesc;
	MediaDesc		mediaDesc[32];
	int				iDescSize;
}CameraDesc;
//
//
////////////////////////////////////////////////////////////////////////


////////////////////////////////////////////////////////////////////////
//
// ��������������
// ������̨����,����Ŀ���������data��,�ɱ������Լ�����
//
typedef struct tagEncoderControl
{
	CommandHead dwCommandHead;					//��������:COMMAND_ENCODER_CONTROL
	DWORD		dwCameraSign;
	BYTE		bControlData[512];
}EncoderControl,*pEncoderControl;
//
//
////////////////////////////////////////////////////////////////////////


#endif	//PROTO_H
