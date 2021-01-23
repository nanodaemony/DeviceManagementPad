package com.nano.http;

import com.alibaba.fastjson.JSON;
import com.nano.activity.evaluation.DeviceEvaluationTable;
import com.nano.activity.mark.MarkEvent;
import com.nano.common.logger.Logger;
import com.nano.common.threadpool.core.TaskExecutor;
import com.nano.device.MedicalDevice;
import com.nano.http.entity.CommonResult;
import com.nano.http.entity.ParamPad;

import java.util.HashMap;
import java.util.Map;

import cn.hutool.http.HttpUtil;

/**
 * Description: 网络管理器
 *
 * @version: 1.0
 * @author: nano
 * @date: 2020/12/30 17:16
 */
public class HttpManager {

    private static Logger logger = new Logger("[NetworkManager]");

    /**
     * 与服务通信的路径，默认为生产环境路径
     */
    public static String SERVER_IP = ServerIpEnum.LOCAL_WIFI.getPath();

    /**
     * HTTP响应处理器
     */
    private HttpHandler httpHandler;

    public HttpManager(HttpHandler httpHandler) {
        this.httpHandler = httpHandler;
    }

    /**
     * 获取网络状态(√)
     */
    public void getNetworkStatus() {
        String path = SERVER_IP + ServerPathEnum.QUERY_NETWORK_STATUS.getPath();
        TaskExecutor.executeHttpTask(() -> {
            logger.info("获取服务器状态");
            try {
                String res = HttpUtil.get(path);
                CommonResult commonResult = JSON.parseObject(res, CommonResult.class);
                if (commonResult != null) {
                    logger.info(res);
                    httpHandler.handleSuccessfulHttpMessage(new HttpMessage());
                }
            } catch (Exception e) {
                // 网络异常
                httpHandler.handleNetworkFailedMessage();
            }
        });
    }


    /**
     * 上传仪器医疗仪器信息并得到采集场次号
     *
     * @param medicalDevice 开始采集的仪器信息
     */
    public void postMedicalDeviceInfo(MedicalDevice medicalDevice) {
        String path = SERVER_IP + ServerPathEnum.POST_MEDICAL_DEVICE_INFO.getPath();
        TaskExecutor.executeHttpTask(() -> {
            try {
                logger.info("上传开始医疗仪器信息:" + medicalDevice.getDeviceName());
                String res = HttpUtil.post(path, new ParamPad(medicalDevice.getCollectionNumber(), JSON.toJSONString(medicalDevice)).generatePostString());
                logger.info(res);
                CommonResult commonResult = JSON.parseObject(res, CommonResult.class);
                if (commonResult != null) {
                    logger.info("获取采集场次号:" + commonResult.toString());
                    // 解析采集场次号并设置
                    medicalDevice.setCollectionNumber(Integer.parseInt(commonResult.getData()));
                    // 传出成功消息
                    httpHandler.handleSuccessfulHttpMessage(new HttpMessage(ServerPathEnum.POST_MEDICAL_DEVICE_INFO, commonResult.getData()));
                } else {
                    httpHandler.handleFailedHttpMessage(new HttpMessage());
                }
            } catch (Exception e) {
                httpHandler.handleNetworkFailedMessage();
            }
        });
    }

    /**
     * 上传仪器数据采集开始
     *
     * @param device 开始采集的仪器信息
     */
    public void postDeviceCollectionStart(MedicalDevice device) {
        String path = SERVER_IP + ServerPathEnum.DEVICE_START_COLLECTION.getPath();
        TaskExecutor.executeHttpTask(() -> {
            try {
                logger.info("上传开始采集信息:" + device.getDeviceName());
                // 携带本次开始的采集场次号
                String res = HttpUtil.post(path, new ParamPad(device.getCollectionNumber()).generatePostString());
                logger.info(res);
                CommonResult commonResult = JSON.parseObject(res, CommonResult.class);
                if (commonResult != null) {
                    logger.info("同意开始采集,仪器号为:" + device.getDeviceName() + ":" + device.getDeviceCode());
                    // 传出成功消息(传出采集场次号)
                    httpHandler.handleSuccessfulHttpMessage(new HttpMessage(ServerPathEnum.DEVICE_START_COLLECTION, commonResult.getData()));
                } else {
                    httpHandler.handleFailedHttpMessage(new HttpMessage());
                }
            } catch (Exception e) {
                httpHandler.handleNetworkFailedMessage();
            }
        });
    }


    /**
     * 上传某仪器结束采集的信息
     */
    public void postDeviceCollectionStop(MedicalDevice device) {
        String path = SERVER_IP + ServerPathEnum.DEVICE_FINISH_COLLECTION.getPath();
        TaskExecutor.executeHttpTask(() -> {
            try {
                logger.info("上传结束采集信息:" + device.getDeviceName());
                // 携带本次结束的采集场次号
                String res = HttpUtil.post(path, new ParamPad(device.getCollectionNumber()).generatePostString());
                logger.info(res);
                CommonResult commonResult = JSON.parseObject(res, CommonResult.class);
                if (commonResult != null) {
                    logger.info("同意结束采集,仪器号为:" + device.getDeviceName() + ":" + device.getDeviceCode());
                    // 传出成功消息(传出采集场次号)
                    httpHandler.handleSuccessfulHttpMessage(new HttpMessage(ServerPathEnum.DEVICE_FINISH_COLLECTION, commonResult.getData()));
                } else {
                    httpHandler.handleFailedHttpMessage(new HttpMessage());
                }
            } catch (Exception e) {
                httpHandler.handleNetworkFailedMessage();
            }
        });
    }


    /**
     * 上传某仪器放弃采集的信息
     */
    public void postDeviceCollectionAbandon(MedicalDevice device) {
        String path = SERVER_IP + ServerPathEnum.DEVICE_ABANDON_COLLECTION.getPath();
        TaskExecutor.executeHttpTask(() -> {
            try {
                logger.info("上传放弃采集信息:" + device.getDeviceName());
                // 携带本次开始的采集场次号
                String res = HttpUtil.post(path, new ParamPad(device.getCollectionNumber()).generatePostString());
                logger.info(res);
                CommonResult commonResult = JSON.parseObject(res, CommonResult.class);
                if (commonResult != null) {
                    logger.info("同意放弃采集,仪器号为:" + device.getDeviceName() + ":" + device.getDeviceCode());
                    // 传出放弃消息(传出采集场次号)
                    httpHandler.handleSuccessfulHttpMessage(new HttpMessage(ServerPathEnum.DEVICE_ABANDON_COLLECTION, commonResult.getData()));
                } else {
                    httpHandler.handleFailedHttpMessage(new HttpMessage());
                }
            } catch (Exception e) {
                httpHandler.handleNetworkFailedMessage();
            }
        });
    }

    /**
     * 上传标记信息
     */
    public void postMarkEvent(MarkEvent markEvent) {
        String path = SERVER_IP + ServerPathEnum.OPERATION_MARK_EVENT_ADD.getPath();
        TaskExecutor.executeHttpTask(() -> {
            try {
                logger.info("上传标记信息");
                String res = HttpUtil.post(path, JSON.toJSONString(markEvent));
                logger.info(res);
                CommonResult commonResult = JSON.parseObject(res, CommonResult.class);
                if (commonResult != null) {
                    logger.info("收到标记信息:" + commonResult.getData());
                    // data存放的是UniqueNumber
                    httpHandler.handleSuccessfulHttpMessage(new HttpMessage(ServerPathEnum.OPERATION_MARK_EVENT_ADD, commonResult.getData()));
                } else {
                    httpHandler.handleFailedHttpMessage(new HttpMessage(ServerPathEnum.OPERATION_MARK_EVENT_ADD));
                }
            } catch (Exception e) {
                httpHandler.handleNetworkFailedMessage();
            }
        });
    }

    /**
     * 上传手术后对仪器的评价信息
     */
    public void postDeviceEvaluationInfo(DeviceEvaluationTable evaluationTable) {
        String path = SERVER_IP + ServerPathEnum.AFTER_COLLECTION_EVALUATION_TABLE_ADD.getPath();
        TaskExecutor.executeHttpTask(() -> {
            try {
                logger.info("上传仪器评价信息");
                String res = HttpUtil.post(path, JSON.toJSONString(evaluationTable));
                logger.info(res);
                CommonResult commonResult = JSON.parseObject(res, CommonResult.class);
                if (commonResult != null) {
                    logger.info("收到仪器评价信息:" + commonResult.getData());
                    // 传出成功消息
                    httpHandler.handleSuccessfulHttpMessage(new HttpMessage(ServerPathEnum.AFTER_COLLECTION_EVALUATION_TABLE_ADD, commonResult.getData()));
                } else {
                    httpHandler.handleFailedHttpMessage(new HttpMessage(ServerPathEnum.AFTER_COLLECTION_EVALUATION_TABLE_ADD));
                }
            } catch (Exception e) {
                httpHandler.handleNetworkFailedMessage();
            }
        });
    }


    /**
     * 上传仪器数据
     */
    public void postDeviceData(String deviceString) {
//        RequestCodeEnum codeEnum = RequestCodeEnum.POST_DEVICE_DATA;
//        int requestCode = codeEnum.getCode();
//        TaskExecutor.executeHttpTask(() -> {
//            try {
//                logger.debug("Device Data:" + deviceString);
//                String res = HttpUtil.post(SERVER_DATA_PATH, new ParamCollector(requestCode, deviceString).generatePostString());
//                CommonResult commonResult = JSON.parseObject(res, CommonResult.class);
//                if (commonResult != null) {
//                    ResultVo resultVo = JSON.parseObject(commonResult.getData(), ResultVo.class);
//                    if (resultVo != null && requestCode == resultVo.getCode()) {
//                        logger.debug("OK." + resultVo.getData());
//                        // 传出成功消息
//                        httpHandler.handleSuccessfulHttpMessage(new HttpMessage(codeEnum, resultVo.getData()));
//                    } else {
//                        httpHandler.handleFailedHttpMessage(new HttpMessage(codeEnum));
//                    }
//                }
//            } catch (Exception e) {
//                httpHandler.handleNetworkFailedMessage();
//            }
//        });
    }


    /**
     * 上传采集采集中的错误日志信息
     */
    public void postCollectorErrorInfo(String errorInfo) {
//        RequestCodeEnum codeEnum = RequestCodeEnum.POST_COLLECTOR_ERROR_INFO;
//        int requestCode = codeEnum.getCode();
//        TaskExecutor.executeHttpTask(() -> {
//            try {
//                logger.info("上传采集器错误信息");
//                String res = HttpUtil.post(SERVER_IP,
//                        new ParamCollector(requestCode, errorInfo).generatePostString());
//            } catch (Exception e) {
//                httpHandler.handleNetworkFailedMessage();
//            }
//        });
    }


    /**
     * 查询常用标记信息列表
     */
    public void getOftenUseMarkEventList() {
        String path = SERVER_IP + ServerPathEnum.GET_OFTEN_USE_MARK_EVENT_LIST.getPath();
        TaskExecutor.executeHttpTask(() -> {
            try {
                logger.info("查询常用标记信息列表");
                String res = HttpUtil.get(path);
                logger.info(res);
                CommonResult commonResult = JSON.parseObject(res, CommonResult.class);
                if (commonResult != null) {
                    logger.info("查询常用标记信息列表:" + commonResult.getData());
                    httpHandler.handleSuccessfulHttpMessage(new HttpMessage(ServerPathEnum.GET_OFTEN_USE_MARK_EVENT_LIST, commonResult.getData()));
                } else {
                    httpHandler.handleFailedHttpMessage(new HttpMessage(ServerPathEnum.GET_OFTEN_USE_MARK_EVENT_LIST));
                }
            } catch (Exception e) {
                httpHandler.handleNetworkFailedMessage();
            }
        });
    }


    /**
     * 查询标记信息列表
     */
    public void getMatchedMarkEventList(String keyWord) {
        String path = SERVER_IP + ServerPathEnum.SEARCH_MATCH_MARK_EVENT_LIST.getPath();
        TaskExecutor.executeHttpTask(() -> {
            try {
                logger.info("查询匹配标记信息列表");
                Map<String, Object> params = new HashMap<>();
                params.put("keyword", keyWord);
                String res = HttpUtil.get(path, params);
                logger.info(res);
                CommonResult commonResult = JSON.parseObject(res, CommonResult.class);
                if (commonResult != null) {
                    logger.info("查询匹配标记信息列表:" + commonResult.getData());
                    httpHandler.handleSuccessfulHttpMessage(new HttpMessage(ServerPathEnum.SEARCH_MATCH_MARK_EVENT_LIST, commonResult.getData()));
                } else {
                    httpHandler.handleFailedHttpMessage(new HttpMessage(ServerPathEnum.SEARCH_MATCH_MARK_EVENT_LIST));
                }
            } catch (Exception e) {
                httpHandler.handleNetworkFailedMessage();
            }
        });
    }


    /**
     * 上传其他自定义标记信息
     */
    public void postAddCustomizeMarkEvent(String markEvent) {
        String path = SERVER_IP + ServerPathEnum.ADD_CUSTOMIZE_MARK_EVENT.getPath();
        TaskExecutor.executeHttpTask(() -> {
            try {
                logger.info("上传其他新增的标记信息");
                String res = HttpUtil.post(path, markEvent);
                logger.info(res);
                CommonResult commonResult = JSON.parseObject(res, CommonResult.class);
                if (commonResult != null) {
                    logger.info("上传其他新增的标记信息:" + commonResult.getData());
                    httpHandler.handleSuccessfulHttpMessage(new HttpMessage(ServerPathEnum.ADD_CUSTOMIZE_MARK_EVENT, commonResult.getData()));
                } else {
                    httpHandler.handleFailedHttpMessage(new HttpMessage(ServerPathEnum.ADD_CUSTOMIZE_MARK_EVENT));
                }
            } catch (Exception e) {
                httpHandler.handleNetworkFailedMessage();
            }
        });
    }


    /**
     * 更新标记事件的时间
     */
    public void postModifyMarkEventTime(MarkEvent event) {
        String path = SERVER_IP + ServerPathEnum.OPERATION_MARK_EVENT_UPDATE.getPath();
        TaskExecutor.executeHttpTask(() -> {
            try {
                logger.info("更新标记事件的时间");
                String res = HttpUtil.post(path, JSON.toJSONString(event));
                logger.info(res);
                CommonResult commonResult = JSON.parseObject(res, CommonResult.class);
                if (commonResult != null) {
                    logger.info("更新标记事件的时间:" + commonResult.getData());
                    httpHandler.handleSuccessfulHttpMessage(new HttpMessage(ServerPathEnum.OPERATION_MARK_EVENT_UPDATE, commonResult.getData()));
                } else {
                    httpHandler.handleFailedHttpMessage(new HttpMessage(ServerPathEnum.OPERATION_MARK_EVENT_UPDATE));
                }
            } catch (Exception e) {
                httpHandler.handleNetworkFailedMessage();
            }
        });
    }

    /**
     * 删除标记事件
     */
    public void postDeleteMarkEvent(MarkEvent event) {
        String path = SERVER_IP + ServerPathEnum.OPERATION_MARK_EVENT_DELETE.getPath();
        TaskExecutor.executeHttpTask(() -> {
            try {
                logger.info("删除标记事件");
                String res = HttpUtil.post(path, JSON.toJSONString(event));
                logger.info(res);
                CommonResult commonResult = JSON.parseObject(res, CommonResult.class);
                if (commonResult != null) {
                    logger.info("删除标记事件:" + commonResult.getData());
                    httpHandler.handleSuccessfulHttpMessage(new HttpMessage(ServerPathEnum.OPERATION_MARK_EVENT_DELETE, commonResult.getData()));
                } else {
                    httpHandler.handleFailedHttpMessage(new HttpMessage(ServerPathEnum.OPERATION_MARK_EVENT_DELETE));
                }
            } catch (Exception e) {
                httpHandler.handleNetworkFailedMessage();
            }
        });
    }

}
