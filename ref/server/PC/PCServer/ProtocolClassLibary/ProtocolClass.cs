using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

/// <summary>
/// 本命名空间为青囊软件的协议描述部分的命名空间。
/// </summary>
namespace QingNang.ProtocolClass
{
    /// <summary>
    /// 客户端请求的基类。
    /// </summary>
    /// <remarks>
    /// op域指定了本次请求的类型。其说明如下：
    /// 如果op=1，表示请求xml数据。其对应的请求格式由<see cref="RequestMedicineXml">RequestMedicineXml</see>描述。而服务器端的应答则由<see cref="RequestMedicineXml_Answer">RequestMedicineXml_Answer</see>描述。
    /// 如果op=2，表示用户添加了新的提醒。其对应的请求格式由<see cref="RequestAddRemind">RequestAddRemind</see>描述。而服务器端的应答则由<see cref="RequestAddRemind">RequestAddRemind_Answer</see>描述。
    /// 如果op=3，表示请求药品的照片。其对应的请求格式由<see cref="RequestPicture">RequestPicture</see>描述。而服务器端的应答则由<see cref="RequestPicture_Answer">RequestPicture_Answer</see>描述。
    /// 如果op=4，表示请求推荐药品。其对应的请求格式由<see cref="RequestSuggest">RequestSuggest</see>描述。而服务器端的应答则由<see cref="RequestSuggest_Answer">RequestSuggest_Answer</see>描述。
    /// 如果op=5，表示查询软件更新。其对应的请求格式由<see cref="RequestVer">RequestVer</see>描述。而服务器端的应答则由<see cref="RequestVer_Answer">RequestVer_Answer</see>描述。
    /// 如果op=6，表示上传本地存在的所有xml文件的扫描码，并且Server端的ul位置位而发起。其对应的请求格式由<see cref="RequestUploadXml">RequestUploadXml</see>描述。而服务器端的应答则由<see cref="RequestUploadXml_Answer">RequestUploadXml_Answer</see>描述。
    /// 如果op=7，表示上传本地存在的所有xml文件的扫描码，并且Client端发起。其对应的请求格式由<see cref="RequestUploadXml">RequestUploadXml</see>描述。而服务器端的应答则由<see cref="RequestUploadXml_Answer">RequestUploadXml_Answer</see>描述。
    /// 如果op=8，表示修改用户名。其对应的请求格式由<see cref="RequestEditName">RequestEditName</see>描述。而服务器端的应答则由<see cref="RequestEditName_Answer">RequestEditName_Answer</see>描述。
    /// </remarks>
    public class RequestBase
    {
        /// <summary>
        /// 请求协议做遵循的版本号。请设为1。
        /// </summary>
        /// <value>请求协议做遵循的版本号。请设为1。</value>
        public int v { get; set; }
        /// <summary>
        /// 请求的操作码。
        /// </summary>
        /// <value>请求的操作码。</value>
        public int op { get; set; }
    }

    /// <summary>
    /// 服务器应答类的基类。
    /// </summary>
    public class AnswerBase
    {
        /// <summary>
        /// 服务器端应答。
        /// </summary>
        /// <value>true表示成功，false表示失败。</value>
        public bool cs { get; set; }
    }

    /// <summary>
    /// 药品xml请求的一个项。其对应op=2。
    /// </summary>
    public class MedicineRequestItem
    {
        /// <summary>
        /// 请求的药品一维码。
        /// </summary>
        /// <value>一维码字符串。</value>
        public string pd { get; set; }
        /// <summary>
        /// 本地是否已经有了。true表示已经有了，false表示没有需要服务器给xml。
        /// </summary>
        /// <value>true表示已经有了，false表示没有需要服务器给xml。</value>
        public bool ow { get; set; }
    }

    /// <summary>
    /// 请求xml数据文件。
    /// </summary>
    /// <remarks>
    /// 每次用户新添加了药品（不管是通过扫描一维码还是手工输入一维码），即使该药的xml在本地已经存在，也需要发送该请求。
    /// </remarks>
    public class RequestMedicineXml : RequestBase
    {
        /// <summary>
        /// 用户唯一标识，格式为："android设备机器码/用户姓名"。
        /// </summary>
        /// <value>用户唯一标识。</value>
        public string uid { get; set; }
        /// <summary>
        /// 药品xml请求项的一个数组。
        /// </summary>
        /// <value>药品xml请求项的一个数组。</value>
        public MedicineRequestItem[] dt { get; set; }
    }

    /// <summary>
    /// 服务器响应药品xml请求中的一个子项。
    /// </summary>
    public class MedicineInfo
    {
        /// <summary>
        /// 药品一维码。
        /// </summary>
        /// <value>药品一维码。</value>
        public string pd { get; set; }
        /// <summary>
        /// 字符串形式保存的xml文件内容。
        /// </summary>
        /// <value>xml文件内容。</value>
        public string xmldt { get; set; }
    }

    /// <summary>
    /// 服务器应答药品xml请求。
    /// </summary>
    public class RequestMedicineXml_Answer : AnswerBase
    {
        /// <summary>
        /// 该数组存放成功找到的xml且请求时ow为false的药品的信息。
        /// </summary>
        /// <value>The dt.</value>
        public MedicineInfo[] dt { get; set; }
        /// <summary>
        /// 该数组存放未查询到的药品的扫描码。如果全部都找到了，那么这个为空数组。
        /// </summary>
        /// <value>该数组存放未查询到的药品的扫描码。</value>
        public string[] nf { get; set; }
        /// <summary>
        /// 是否需要上传所有本地已经存在的xml的扫描码。
        /// </summary>
        /// <value>true表示是，false表示否。</value>
        public bool ul { get; set; }
    }

    /// <summary>
    /// 提醒类型，其为从0开始的整型。
    /// </summary>
    public enum RemindType
    {
        /// <summary>
        /// 每日一次。
        /// </summary>
        OnceADay = 0,
        /// <summary>
        /// 每日二次。
        /// </summary>
        TwiceADay,
        /// <summary>
        /// 每日三次。
        /// </summary>
        ThreeTimesADay,
        /// <summary>
        /// 每日四次。
        /// </summary>
        FourTimesADay,
        /// <summary>
        /// 饭前饭后。
        /// </summary>
        BeforeAndAfter,
        /// <summary>
        /// 自设提醒。
        /// </summary>
        SeflSet,
        /// <summary>
        /// 未知项。
        /// </summary>
        Nothing
    }


    /// <summary>
    /// 提醒的一个项。
    /// </summary>
    public class RemindInfo
    {
        /// <summary>
        /// 设置提醒的药品一维码。
        /// </summary>
        /// <value>设置提醒的药品一维码。</value>
        public string pd { get; set; }
        /// <summary>
        /// 提醒类型。其为一个从0开始的整数，参见<see cref="RemindType">RemindType</see>。
        /// </summary>
        /// <value>提醒类型。</value>
        public int tp { get; set; }
        /// <summary>
        /// 用药数量。
        /// </summary>
        /// <value>用药数量。</value>
        public double nb { get; set; }
        /// <summary>
        /// 如果是规律用药，则是前面的格式（即只有年月日）表示开始时间；如果是按需用药，则是后面的格式，有具体到时间。如果有多个时间，则用"/"隔开，如2012-12-12 12:22:33/2012-12-12 12:22:34。
        /// </summary>
        /// <value>提醒时间。</value>
        public string st { get; set; }
        /// <summary>
        /// 如果是规律用药，则是该格式；如果是按需用药，则该项为null。
        /// </summary>
        /// <value>提醒时间。</value>
        public string et { get; set; }
    }

    /// <summary>
    /// 添加提醒之后，与服务器通信的格式。
    /// </summary>
    public class RequestAddRemind : RequestBase
    {
        /// <summary>
        /// 用户唯一标识，格式为："android设备机器码/用户姓名"。
        /// </summary>
        /// <value>用户唯一标识。</value>
        public string uid { get; set; }
        /// <summary>
        /// 提醒项的一个数组。
        /// </summary>
        /// <value>The dt.</value>
        public RemindInfo[] dt { get; set; }
    }

    /// <summary>
    /// 添加提醒之后，与服务器通信的应答。
    /// </summary>
    public class RequestAddRemind_Answer : AnswerBase
    {
    }

    /// <summary>
    /// 请求图片的请求格式。1.如果用户自己拍摄了本地的照片或者用户设置了不从服务器获取图片（因为这个比较耗流量，因此应当有这么个设计），则忽略此请求。2.如果用户没有自己拍摄并设置了需要从服务器获取照片，那么android端则自动发起每一个没有图片的药的图片请求。
    /// </summary>
    public class RequestPicture : RequestBase
    {
        /// <summary>
        /// 需要请求药品的照片的扫描码。
        /// </summary>
        /// <value>需要请求药品的照片的扫描码。</value>
        public string dt { get; set; }
    }

    /// <summary>
    /// 请求照片的应答格式。
    /// </summary>
    public class RequestPicture_Answer : AnswerBase
    {
        /// <summary>
        /// 图片实际数据。由于json格式里面，默认不支持byte类型，但是在pc上还是将其按照一个字节一个字节传下去。字节的值为0-255。
        /// </summary>
        /// <value>图片实际数据。</value>
        public uint[] dt { get; set; }
        /// <summary>
        /// 图片类型。仅仅是该图片在操作系统下的扩展名。
        /// </summary>
        /// <value>图片类型。</value>
        public string tp { get; set; }
    }

    /// <summary>
    /// 请求推荐药品。
    /// </summary>
    public class RequestSuggest : RequestBase
    {
        /// <summary>
        /// 用户唯一标识，格式为："android设备机器码/用户姓名"。
        /// </summary>
        /// <value>用户唯一标识。</value>
        public string uid { get; set; }
        /// <summary>
        /// 上一次请求推荐药品时应答的"nf"数组里面的内容。
        /// </summary>
        /// <value>上一次请求推荐药品时应答的"nf"数组里面的内容。</value>
        public string[] dt { get; set; }
    }

    /// <summary>
    /// 请求推荐药品应答。注，获取了推荐药品应答之后，需要再根据本地没有的xml，再发起一个请求xml的请求。
    /// </summary>
    public class RequestSuggest_Answer : AnswerBase
    {
        /// <summary>
        /// 该数组存放推荐药品的排列顺序。如果本地有的patchcode，在此列表中没有了，则不显示那些patchcode。
        /// </summary>
        /// <value>该数组存放推荐药品的排列顺序。</value>
        public string[] nf { get; set; }
    }

    /// <summary>
    /// 查询软件更新。每次启动软件时check。
    /// </summary>
    public class RequestVer : RequestBase
    {
        /// <summary>
        /// 本机的软件版本号。
        /// </summary>
        /// <value>本机的软件版本号。</value>
        public string vr { get; set; }
        /// <summary>
        /// 用户唯一标识，格式为："android设备机器码/用户姓名"。
        /// </summary>
        /// <value>用户唯一标识。</value>
        public string uid { get; set; }
    }

    /// <summary>
    /// 查询软件更新应答。
    /// </summary>
    public class RequestVer_Answer : AnswerBase
    {
        /// <summary>
        /// 如果需要更新，则为true，否则为false。
        /// </summary>
        /// <value>则为true，否则为false。</value>
        public bool ud { get; set; }
        /// <summary>
        /// 如果<see cref="ud">ud</see>=true，则这里为新软件的版本号。
        /// </summary>
        /// <value>如果<see cref="ud">ud</see>=true，则这里为新软件的版本号。</value>
        public string vr { get; set; }
        /// <summary>
        /// 是否需要上传所有本地已经存在的xml的扫描码。true表示是，false表示否。
        /// </summary>
        /// <value>true表示是，false表示否。</value>
        public bool ul { get; set; }
    }

    /// <summary>
    /// 上传本地存在的所有xml文件的扫描码。
    /// </summary>
    public class RequestUploadXml : RequestBase
    {
        /// <summary>
        /// 本地的所有xml文件扫描码。
        /// </summary>
        /// <value>本地的所有xml文件扫描码。</value>
        public string[] dt { get; set; }
    }

    /// <summary>
    /// 服务器应答上传本地存在的所有xml文件的扫描码。
    /// </summary>
    public class RequestUploadXml_Answer : AnswerBase
    {
        /// <summary>
        /// 需要本地删除xml的药品的扫描码。
        /// </summary>
        /// <value>需要本地删除xml的药品的扫描码。</value>
        public string[] dt { get; set; }
        /// <summary>
        /// 表示如上的所有xml是否是必须删除。具体取值参见remark部分。
        /// </summary>
        /// <value>表示如上的所有xml是否是必须删除。</value>
        /// <remarks>
        /// 表示如上的所有xml是否是必须删除，为一个8位的整数，0b00000000，从右至左，第一位表示是否必须删除（如果为1表示无条件删除该xml，0表示客户端自己判断，主要根据该xml下载时间，如超过3个月没有被访问，则将其删除；否则不用删除。）；第二位表示如果删除该xml的情况下，是否删除图片，包括用户自己照的图片；1表示需要删除，0表示不删除。是这样的：为了避免每个android端软件下载了xml但是服务器上对应的xml文件因为其他原因被更新，那么这个时候，该药品就会出现在上面的patchcode数组中，并且这个时候的xml对应的项在下面这个数组中就为true，表示必须删除了再从服务器上获取。另外而可能服务器上会探测到某些android客户端异常，比如一直在下载我们的xml资料，这个是比较严重的问题，那么服务器端就会发送指令下去，将最早下载的xml删除。
        /// </remarks>
        public int[] nd { get; set; }
    }

    /// <summary>
    /// 修改用户名
    /// </summary>
    public class RequestEditName : RequestBase
    {
        /// <summary>
        /// 修改前的uid。
        /// </summary>
        /// <value>修改前的uid。</value>
        public string uido { get; set; }
        /// <summary>
        /// 修改后的uid。
        /// </summary>
        /// <value>修改后的uid。</value>
        public string uid { get; set; }
    }

    /// <summary>
    /// 修改用户名服务器应答。
    /// </summary>
    public class RequestEditName_Answer : AnswerBase
    {

    }
}
