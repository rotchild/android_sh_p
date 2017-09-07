/*! @proto.h
********************************************************************************
模块名       : StreamServer ControlServer ArchiveServer FileServer 
               ServersManager ...
文件名       : proto.h
相关文件     : 
文件实现功能 : 描述了各模块之间通讯的协议和相关宏定义
				所有字段都遵循网络格式
作者         : ybfan@wtwh.com.cn, xpchen@wtwh.com.cn
版本         : 5.0.0
时间         : 2005/07/07
--------------------------------------------------------------------------------
备注         : <其它说明>
--------------------------------------------------------------------------------
修改记录 : 
日期			版本		修改人					修改内容
2005/07/07		5.0.0	ybfan@wtwh.com.cn		1.IpcReport增加了用户级别		
						xpchen@wtwh.com.cn		2.AlertNotifyFormat和EventActFormat
												3.QuestReject增加CameraSign
												4.服务监控状态报告的修改
				5.0.1	ybfan@wtwh.com.cn		1.增加客户端主动断开连接的命令和宏。
				5.0.2	xpchen@wtwh.com.cn		1.增加客户端向控制服务证明存活的宏
2005/07/11		5.0.3	ybfan@wtwh.com.cn		1.添加状态报告命令宏
												2.添加编码器控制命令
2005/07/11		5.0.4	ybfan@wtwh.com.cn		1.合并流描述和流数据端口修改流数据协议
                5.0.5   xpchen@wtwh.com.cn		1.复用报警处理和手工开始/停止存储结构
2005/07/14		5.0.6	xpchen@wtwh.com.cn		1.添加客户端向控制服务的刷新请求
2005/07/16		5.0.7	xpchen@wtwh.com.cn		1.修改 StreamEncoderStat FileServCameraList
												  增加当前包内有效实体数量
												2.修改 EventActFormat 
2005/07/17		5.0.8	ybfan@wtwh.com.cn		1.修改liveRequest 增加跳数
2005/07/21		5.0.9	ybfan@wtwh.com.cn		1.去掉流代理命令,修改文件格式

*******************************************************************************/
#ifndef PROTO_H
#define PROTO_H

#include <stdlib.h>

#define _MAX_PATH 255
//类型定义
typedef unsigned long				DWORD;
typedef int							BOOL;
typedef unsigned char				BYTE;
typedef unsigned short				WORD;
typedef unsigned int				UINT;

#define FUNCTIONFAIL				-1
#define CHECKTIME					5

#define ARCHIVE_INFO_SIZE			50
#define NAMEPWD_SIZE				32

//数据包大小
//#define PAYLOADSIZE				532

#define SPOOLSIZE					256
#define MAXPAYLOADDATASIZE			2048

#define MAXREQUESTHOPCOUNT			8	//最多经过代理的数目

#define UDPCLIENT					1
#define TCPCLIENT					2


//负载类型描述宏定义
#define VT_NONE						0x00
#define VT_WTMP4					0x01
#define VT_H263						0x02
#define VT_H264						0x03

#define AT_NONE						0x40
#define AT_MP2						0x41
#define AT_ADPCM					0x42
#define AT_UPCM						0x43

#define ST_STATUS					0xFF

//拒绝原因
#define	ILLEGALUSER					0x0002		//非法用户
#define	STREAMNOTFOUND				0x0101		//未发现指定流
#define	FILENOTFOUND				0x0201		//未发现指定文件
#define	ERRORABNORMITY				0xFFFF		//未指定的异常

//Errono
#define ENCORDERNOTEXIST			0x0001		//编码器不存在
#define ENCORDERDUPLICATE			0x0002		//编码器重名

//EncoderType
#define HARDENCORDER				0x01		//实际编码器
#define PROXYENCORDER				0x02		//通过代理方式得到的编码器


//命令版本
#define COMMANDVERSION01			0x01

//文件服务命令定义
#define COMMAND_FILEREQUEST			0x10
#define COMMAND_FILEDESC 			0x11		//文件描述
#define COMMAND_FILEDOWNLOAD		0x12

#define COMMAND_NONE				0xFF		//空操作，不改变任何状态，命令大小只能是4个字节	
#define COMMAND_REJECTSERVICE		0xEE		//拒绝服务

#define COMMAND_LIVESTREAMDESC		0x21		//livestream描述
#define COMMAND_LIVEQUEST			0X22		//请求live
#define COMMAND_DOWNLOADLIVE		0X23		//下载live
#define COMMAND_CLOSECONNECT		0X28		//断开连接


// 均与Ipcamera配置中的定义一致
#define COMMAND_ALERTREPORT			0x00		//（服务器给客户端的）报警报告
#define COMMAND_CLIENTLIVE			0x03		//（客户端给服务器的）请求
#define COMMAND_ALERTPROCESS		0x06		//（客户端给服务器的）报警处理
#define COMMAND_RECORDPROCESS		0x07		//（客户端给服务器的）录像处理
#define COMMAND_CAMERALIST			0x08		//（客户端给服务器的）编码器状态列表
#define COMMAND_FREEDISKLIST		0x09		//（客户端给服务器的）文件服务磁盘空间列表


#define COMMAND_TIME				0x30		//请求对时
#define COMMAND_PROXYREQUEST		0x34		//请求代理分配
#define COMMAND_PROXYRESPONSE		0x35		//代理分配结果


//存储服务命令定义   0xA0 ~ 0xBF
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

//服务管理程序命令定义   0xC0 ~
#define COMMAND_STREAM_STAT			0xC0
#define COMMAND_STREAM_CONFIG		0xC1
#define COMMAND_STREAM_CAMERA		0xC2	//得到streamServer 的 camear列表

#define COMMAND_FILE_IN_STAT		0xD1
#define COMMAND_FILE_OUT_STAT		0xD2
#define COMMAND_FILE_CONFIG			0xD3
#define COMMAND_FILE_DISK			0xD4
#define COMMAND_FILE_CAMERA			0xD5

#define COMMAND_CONTROL_QUESTLIST	0xD6		//服务管理客户端向控制服务请求服务器列表


#define COMMAND_ENCODER_CONTROL		0xE1		//编码器控制命令
#define COMMAND_ENCODER_DATA		0xE2		//编码器数据
#define COMMAND_ENCODER_DESC		0xE3		//编码器描述




//通用命令头结构
typedef	struct tagCommonCommandHead{
	BYTE 	bCommandVersion;					//请求命令版本,当前为0x01
	BYTE 	bCommandType;						//请求命令类型,
	WORD	wCommandLen;						//命令大小
}CommandHead, *pCommandHead;






////////////////////////////////////////////////////////////////////////
//
// 编码器向流服务器发送（流描述和流数据）
//
//Media信息描述，媒体特有描述见下面的两个结构体
typedef struct tagMediaDesc
{
	BYTE 	bMediaID;							//媒体标识 0~31
	BYTE 	bPayload;							//媒体负载类型
	BYTE 	bQualityClass;						//质量等级 0~7,0是最重要的重要性随数字增加而减少
	BYTE 	bBitrate;							//单位8Kbps=1kBYTE/s 0:表示可变码流
	char	pszMediaSpcialInfo[8];				//媒体特有描述根据不同bPayloadtype不同
}MediaDesc, *pMediaDesc;

//特有媒体描述
//wt_mp4,mp4,263类
typedef struct tagVideoMediaInfo
{
	BYTE 	bWidth;								//幅面宽度单位8pixel
	BYTE 	bHeight;							//幅面高度单位8pixel	
	BYTE 	bFrameRate;							//帧率
	BYTE 	bExtend;							//扩展标志 1：有扩展
	WORD	wOffset;							//相对偏移量
	WORD	wSize;								//大小
}VideoMediaInfo, *pVideoMediaInfo;

//mp2,adpcm类
typedef struct tagAudioMediaInfo
{
	DWORD	dwSamplepersecond;					//采样频率
	WORD	wChannel;							//通道号
	WORD	wBitsPerSample;						//采样位数
} AudioMediaInfo, *pAudioMediaInfo;

//Stream流描述，紧接着的是 bMediaNumber 个 MediaDesc
typedef struct tagStreamDesc
{
	CommandHead	dwCommandHead;					//命令类型：COMMAND_LIVESTREAMDESC
	DWORD	dwCameraSign;						//Ipcamera标识
	WORD	wClientID;							//当服务器发送描述给客户端时，该位置填充clinetID
	BYTE 	bMediaNumber;						//该流包含多少Media包 最多32
	BYTE 	bChangeFlag;						//配置改动标识 从0开始每次发生改动＋1
	//MediaDesc	pstMediaDesc[32];				//紧接着若干个流描述
}StreamDesc, *pStreamDesc;

// 流数据头格式，占16个字节，数据 payload 占512字节
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
typedef struct tagPayloadFormatHead				//负载格式
{
	CommandHead	dwCommandHead;			 		//命令类型：COMMAND_ENCODER_DATA
	union
	{
		struct
		{
			DWORD bitReserved	: 8;			//保留字段
			DWORD bitMediaID	: 5;			//媒体ID。此stream中这一Media的编号
			DWORD bitFrameClass	: 3;			//帧的类型。视频帧时：0 -> I帧，1 -> P帧。
												//			音频时为0
			DWORD bitFrameSeq	: 10;			//当前帧的序列号，同一Media的帧序列号依次加1
												//各Media之间的序列号没有关系
			DWORD bitChangeFlag : 6;			//流描述的改变标识，与StreamDesc中的bChangeFlag对应


		};
		DWORD dwFirstDWORD;							//头双字
	};
	BYTE	bPacketIndex;						//包编号。此包在此帧中的序列号，由0开始。
	BYTE	bPackNum;							//包数量。此帧中切分包的个数。
	WORD	wSequenceNum;						//序列数。此包在所有包中的序列号。各Media之间无关系。
	DWORD	dwCameraSign;						//摄像标识。
	DWORD	dwTimestamp;						//时间戳。此帧对应的播放时间戳。
}PayloadFormatHead, *pPayloadFormatHead;
//
//
//
////////////////////////////////////////////////////////////////////////






////////////////////////////////////////////////////////////////////////
//
// 用户与流服务器（流代理服务器）
//

//用户向流服务器请求流数据
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

//拒绝应答命令的格式，**需要考虑是否带上 Sign 信息**
typedef struct tagQuestReject
{
	CommandHead dwCommandHead;					//命令类型:COMMAND_REJECTSERVICE
	DWORD		dwCameraSign;					//拒绝的Ipcamera标识
	WORD		wRejectReason;					//拒绝原因
	WORD		wReserve;
}QuestReject, *pQuestReject;

//用户向服务器的流发布端口发送请求请求数据下载的格式
//请求媒体的等级
typedef struct tagMediaDownloadDesc
{
	BYTE		bRequestMediaID;				//要求的mediaid
	BYTE		bRequestMediaClass;				//要求的media等级
}MediaDownloadDesc;

//请求下载的格式
typedef struct tagStreamDataDownload
{
	CommandHead	dwCommandHead;					//命令类型:COMMAND_DOWNLOADLIVE
	DWORD		dwCameraSign;					//Ipcamera标识
	WORD		wClientID;						//分配给客户的ID
	BYTE		bRequestMediaNum;				//要求的media数量
	BYTE		bReserve;						//保留
	MediaDownloadDesc mediaDownloadDesc[32];
}StreamDataDownload, *pStreamDataDownload;	

//用户断开主动连接
typedef struct tagCloseConnect
{
	CommandHead dwCommandHead;					//命令类型:COMMAND_CLOSECONNECT
	DWORD		dwCameraSign;					//Ipcamera标识
	WORD		wClientID;						//分配的客户ID
	BYTE		bReserve;						//保留
	BYTE		bReserve2;						//保留
}CloseConnect, *pCloseConnect;
//
//
//
////////////////////////////////////////////////////////////////////////






////////////////////////////////////////////////////////////////////////
//
// 编码器与对时服务
//
// 编码器向对时服务发送请求，对时服务向编码器回应时间
typedef struct tagCorrectTime
{
	CommandHead dwCommandHead;						//命令类型:COMMAND_TIME
	DWORD		dwTime;								//自1970.1.1日到现在的秒数格林威治时间
}CorrectTime, *pCorrectTime;
//
//
//
////////////////////////////////////////////////////////////////////////






////////////////////////////////////////////////////////////////////////
//
// 客户端与分配服务
//
// 客户端向分配服务发送请求
typedef struct tagProxyQuest
{
	CommandHead dwCommandHead;						//
	char		pszName[NAMEPWD_SIZE];				//请求代理的用户名
	char		pszPassword[NAMEPWD_SIZE];			//请求代理的密码
}ProxyQuest, *pProxyQuest;

//分配服务向客户端回应代理服务器地址
typedef struct tagProxyAddr
{
	CommandHead dwCommandHead;
	char		pszAddr[20];						//服务器地址
}ProxyAddr, *pProxyAddr;
//
//
//
////////////////////////////////////////////////////////////////////////






////////////////////////////////////////////////////////////////////////
//
// 编码器与流服务、流服务与控制服务、控制服务与客户端、流服务与客户端
//
// 编码器向控制服务发送的状态报告/报警报告/云台命令，具体表示参看文档《Ipcamera配置》
//
// 其中，客户端与控制服务通讯的结构中，tagIpcReport的前面还加有CommandHead头。
// CommandHead头中的wCommandLen为sizeof(tagIpcReport)。
// 与编码器通讯的各结构中均没有CommandHead头。
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
	DWORD	dwCameraSign;							//Ipcamera标识
	WORD	wSequenceNumber;						//命令序列号
	WORD	wReportSize;							//命令大小，不包括 tagIpcReport 的前三项
	BYTE	bReportType;							//命令类型
	BYTE	bUserClass;								//用户级别
	BYTE	bReportContent;							//命令内容1
	BYTE	bReportContent2;						//命令内容2
	char	pszReportInfo[20];						//命令描述
} IpcReport, *pIpcReport;
//
//  上述命令的主要解释		状态报告		报警					云台命令	
//	WORD ReportSize;		0x03		0x04(+20)			0x04	
//	BYTE ReportType;		0x00		0x00				0x02
//  BYTE UserClass;			0x00		0x00				用户等级
//	BYTE ReportContent;		0x00		0x66(外)0x65(内)		云台动作（上下左右...）				
//	BYTE ReportContent2;				端口号				云台速度
//
//
//
//
////////////////////////////////////////////////////////////////////////






////////////////////////////////////////////////////////////////////////
//
// 客户端与文件发布服务
//
//用户向服务器的文件发布端口发送如下格式请求建立连接
typedef struct tagFileRequest
{
	CommandHead	dwCommandHead;					//命令类型:COMMAND_FILEREQUEST
	char		pszName[NAMEPWD_SIZE];			//用户名
	char		pszPassword[NAMEPWD_SIZE];		//密码
	char		pszFileName[256];				//文件名（包括路径）
}FileRequest, *pFileRequest;

//服务器向用户返回文件流信息:
typedef struct tagFileDesc
{
	CommandHead dwCommandHead;					//命令类型:COMMAND_FILEDESC
	DWORD		dwSizeofHead;					//文件头大小
	DWORD		dwSizeofFile;					//文件大小
}FileDesc, *pFileDesc;

//用户向服务器的文件发布端口发送如下格式请求下载文件
typedef struct tagFileDownload
{
	CommandHead	dwCommandHead;					//命令类型:COMMAND_FILEDOWNLOAD
	DWORD		dwBeginOffset;					//请求的文件起始位置
	DWORD		dwDownloadSize;					//请求的字节数
}FileDownload, *pFileDownload;

// 文件头格式
typedef struct tagFileMediaDesc
{
	DWORD		dwCameraSign;					//Ipcamrea标识
	BYTE		bMediaNum;
	BYTE		bReserve[3];
	MediaDesc	mMediainfo[8];
}FileMediaDesc,*pFileMediaDesc;

typedef struct tagFileHead
{
	WORD		wVersion;							//版本号				
	WORD		wHeadSize;							//文件头的大小
	DWORD		dwFileSize;							//文件大小（文件未结束则不填充）
	DWORD		dwPresentionTime;					//播放时间
	BYTE		bStreamNum;							//一共含有几路流
	BYTE		bReserve[3];
	FileMediaDesc	fFileDesc[8];
}FileHead, *pFileHead;
//
//
////////////////////////////////////////////////////////////////////////






////////////////////////////////////////////////////////////////////////
//
// 控制服务与文件存储服务。
// 下列所有描述中的请求(Requst, Req)均是指控制服务向存储服务的请求
// 所有的应答(Response, Resp)均是指存储服务向控制服务的应答
//
//将某一路 sign 分配给存储服务的请求
typedef struct tagAssignIpcReq
{
	CommandHead dwCommandHead;					//
	DWORD		dwCameraSign;					//Ipcamera标识
	char		pszCenterServerIP[20];			//此 Ipcamera 所在的流服务器地址
	WORD		wCenterServerPort;				//流服务器端口
}AssignIpcReq, *pAssignIpcReq;

//将某一路 sign 从存储服务撤销的请求
typedef struct tagRemoveIpcReq
{
	CommandHead dwCommandHead;
	DWORD		dwCameraSign;					//Ipcamera标识
}RemoveIpcReq, *pRemoveIpcReq;

//对某一路 sign 要求开始存储的请求
typedef struct tagArchiveReq
{
	CommandHead dwCommandHead;
	DWORD		dwPolicyID;						//存储请求ID
	DWORD		dwCameraSign;					//Ipcamera标识
	DWORD		dwEventStartTime;				//存储开始时间
	DWORD		dwEventStopTime;				//存储结束时间
}ArchiveReq, *pArchiveReq;

//某一路 sign 开始存储数据的应答
typedef struct tagArchiveResp
{
	CommandHead dwCommandHead;					
	DWORD		dwPolicyID;						//存储请求ID
	DWORD		dwCameraSign;					//Ipcamera标识
	char		pszFileName[_MAX_PATH];			//存储文件名
}ArchiveResp, *pArchiveResp;

//某一路 sign 改变存储策略的请求
typedef struct tagChgArchReq
{
	CommandHead dwCommandHead;
	DWORD		dwPolicyID;						//存储请求ID
	DWORD		dwCameraSign;					//Ipcamera标识
	DWORD		dwEventStopTime;				//存储结束时间
}ChgArchReq, *pChgArchReq;


//某一个存储策略完成的应答
typedef struct tagPolicyEndResp
{
	CommandHead dwCommandHead;
	DWORD		dwPolicyID;						//存储请求ID
	DWORD		dwCameraSign;					//Ipcamera标识 
}PolicyEndResp, *pPolicyEndResp;

//某一个编码器断线的请求
typedef struct tagIpcLostReq
{
	CommandHead dwCommandHead;
	DWORD		dwCameraSign;					//Ipcamera标识
}IpcLostReq, *pIpcLostReq;

//手动停止存储的请求
typedef struct tagHandStopReq
{
	CommandHead dwCommandHead;
	DWORD		dwPolicyID;						//存储请求ID
	DWORD		dwCameraSign;					//Ipcamera标识
}HandStopReq, *pHandStopReq;

//删除文件的请求
typedef struct tagDelFileReq
{
	CommandHead dwCommandHead;
	char		pszFileName[_MAX_PATH];			//请求删除的文件名
}DelFileReq, *pDelFileReq;

//删除文件的应答
typedef struct tagDelFileResp
{
	CommandHead dwCommandHead;
	char		pszFileName[_MAX_PATH];			//请求删除的文件名
	BYTE		bFinished;						//删除成功或者失败，成功为1，失败为0
}DelFileResp, *pDelFileResp;

//删除记录的应答
typedef struct tagDelRecordResp
{
	CommandHead dwCommandHead;
	char		pszFileName[_MAX_PATH];			//请求删除这个文件相关的文件记录
}DelRecordResp, *pDelRecordResp;
//
//
////////////////////////////////////////////////////////////////////////






////////////////////////////////////////////////////////////////////////
//
// 服务管理程序与流服务/流代理服务
// 需要状态报告的程序先向相关服务器发送状态请求，只填充CommnadHead
// 各服务接收到请求后填充状态报告返回


//流服务/流代理服务向服务管理程序报告配置情况
//服务管理程序向流服务/流代理服务发送配置改变
typedef struct tagStreamServConfig
{
	CommandHead dwCommandHead;					
	WORD		wSourceStreamPort;				//编码器发送数据端口号
	WORD		wQuestStreamPort;				//用户请求数据端口号
}StreamServConfig, *pStreamServConfig;


//流服务器服务编码器列表
typedef struct tagEncoderList
{
	CommandHead dwCommandHead;					//命令类型:COMMAND_STREAM_CAMERA
	WORD		wOnlineIpcNum;					//当前在线编码器个数
	WORD		wStartSeq;						//本包开始位置
	WORD		wActualIpcNum;					//本包中的编码器个数
	WORD		wReserved;
	DWORD		dwCameraSign[100];				//CameraSign列表 注：因为编码器列表可能很大，考虑到一次可能发送不完，
												//所以加入wStartseq.每个包最多不超过10 Encorder
	BYTE		bEncorderType[100];				//encoder类型
}EncoderList, *pEncoderList;

//
////////////////////////////////////////////////////////////////////////






////////////////////////////////////////////////////////////////////////
//
// 服务管理程序与文件服务
//
//文件发布服务向服务管理程序报告状态


//文件发布服务向服务管理程序报告配置情况
//服务管理程序向文件发布服务发送配置改变
typedef struct tagFileServConfig
{
	CommandHead dwCommandHead;
	WORD		wListenPort;					//文件发布服务监听端口号
	WORD		wClientBrokenTime;				//用户断线时间
}FileServConfig, *pFileServConfig;

//流服务器服务编码器列表
typedef struct tagCameraStat 
{
	DWORD		dwCameraSign;
	BYTE		bState;							//是否存储
	BYTE		bReason;						//存储原因
	WORD		wReserve;
}CameraStat, *pCameraStat;

typedef struct tagFileServCameraList
{
	CommandHead dwCommandHead;
	WORD		wOnlineIpcNum;					//当前在线编码器个数
	WORD		wStartSeq;						//本包开始位置
	WORD		wActualIpcNum;					//本包中的编码器个数
	WORD		wReserved;
//	CameraState cState[wOnlineIpcNum];			//CameraSign列表 注：因为编码器列表可能很大考虑到一次可能发送不完，
												//所以加入wStartseq.推荐udp发送不要超过1k大小
}FileServCameraList, *pFileServCameraList;

//
//
////////////////////////////////////////////////////////////////////////


////////////////////////////////////////////////////////////////////////
//
// 服务管理客户端与控制服务
//
//服务管理客户端向控制服务请求系统内所有数据服务和文件服务地址信息
typedef struct tagServerMeta
{
	char		pszServerIP[20];						// 数据服务或者文件服务地址
	WORD		wServerPort;							// 服务状态监听端口号
	WORD		wServerType;							// 服务类型， 0：数据服务， 1：文件服务
}ServerMeta, *pServerMeta;

typedef struct tagServersInfo
{
	CommandHead dwCommandHead;							// 其中的 wCommandLen 包括 servers[dwServerNum]的大小
	DWORD		dwServerNum;							// 服务实体的数量
//	ServerMeta	servers[dwServerNum];
}ServersInfo, *pServersInfo;
//
//
////////////////////////////////////////////////////////////////////////



////////////////////////////////////////////////////////////////////////
//
// 客户端与控制服务
//
// 通知给客户端的报警结构
typedef struct tagAlertNotifyFormat
{
	CommandHead dwCommandHead;
	DWORD		dwCameraSign;						//摄像标识
	WORD		wSequence;							//LT序号，识别是否同一报警
	WORD		wAlertPortID;						//报警端口ID
	DWORD		dwEventTypeID;						//报警事件类型
	DWORD		dwEventLogID;						//报警记录ID
	char		pszEventType[20];					//报警事件描述					
}AlertNotifyFormat, *pAlertNotifyFormat;


//客户端向控制服务请求总体结构
typedef struct tagClientQuest
{
	CommandHead dwCommandHead;						// 其中的 wCommandLen 包括 后续对象的大小
	DWORD		dwObjectNum;						// 对象数量，对象可能是CameraLiveStat，DiskFreeSize
}ClientQuest, *pClientQuest;

//编码器状态列表对象
typedef struct tagCameraLiveStat
{
	DWORD		dwCameraSign;						//Ipcamera标识
	BYTE		bOnline;							//是否在线
	BYTE		bStore;								//是否正在存储
	BYTE		bReserved;
	BYTE		bReserved2;
}CameraLiveStat, *pCameraLiveStat;

//
//
////////////////////////////////////////////////////////////////////////



////////////////////////////////////////////////////////////////////////
//
// 流服务/流代理服务专用
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
// 编码器控制命令
// 包括云台命令,具体的控制数据在data区,由编码器自己解释
//
typedef struct tagEncoderControl
{
	CommandHead dwCommandHead;					//命令类型:COMMAND_ENCODER_CONTROL
	DWORD		dwCameraSign;
	BYTE		bControlData[512];
}EncoderControl,*pEncoderControl;
//
//
////////////////////////////////////////////////////////////////////////


#endif	//PROTO_H
