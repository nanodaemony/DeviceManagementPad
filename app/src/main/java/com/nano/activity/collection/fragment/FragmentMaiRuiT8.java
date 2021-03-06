package com.nano.activity.collection.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.nano.R;
import com.nano.datacollection.parsedata.DataCons;
import com.nano.datacollection.parsedata.entity.DataMaiRuiT8;
import com.nano.activity.collection.interfaces.FragmentDataExchanger;
import com.nano.activity.collection.interfaces.FragmentOperationHandler;
import com.nano.activity.collection.MessageEntity;
import com.nano.datacollection.CollectionStatusEnum;
import com.nano.device.DeviceEnum;
import com.nano.device.DeviceUtil;
import com.nano.device.MedicalDevice;

import androidx.annotation.NonNull;

/**
 * Description: 迈瑞BeneViewT8数据采集器的Fragment
 *
 * @version: 1.0
 * @author: nano
 * @date: 2020/12/29 13:46
 */
public class FragmentMaiRuiT8 extends android.support.v4.app.Fragment implements FragmentDataExchanger {

    /**
     * 仪器图片
     */
    private ImageView ivDeviceImage;
    private TextView tvControlMessage;

    /**
     * 采集状态
     */
    private TextView tvCollectionStatus;

    /**
     * 接收计数器与成功上传计数器
     */
    private TextView tvReceiveCounter;
    private TextView tvSuccessfulUpdateCounter;


    /**
     * 操作Fragment的操作器
     */
    private FragmentOperationHandler handler;

    /**
     * 对应的仪器信息
     */
    private MedicalDevice device;



    private TextView tvDataEcgHr;
    private TextView tvDataResp;
    private TextView tvDataCvp;
    private TextView tvDataArt;
    private TextView tvDataNibp;
    private TextView tvDataTemp;
    private TextView tvDataSpo2;
    private TextView tvDataSpo2Pr;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_collection_mairui_bene_view_t8, container, false);

        tvCollectionStatus = root.findViewById(R.id.device_collection_status);
        tvReceiveCounter = root.findViewById(R.id.collection_receive_counter);
        tvSuccessfulUpdateCounter = root.findViewById(R.id.successful_update_counter);
        // 仪器图片
        ivDeviceImage = root.findViewById(R.id.device_collection_device_image);
        // 控制信息
        tvControlMessage = root.findViewById(R.id.device_collection_control_message);
        ivDeviceImage.setOnClickListener(view -> {
            if (device.getStatusEnum() == CollectionStatusEnum.WAITING_START) {
                handler.handleDeviceStartCollection(new MessageEntity(device.getDeviceCode()));
            } else if (device.getStatusEnum() == CollectionStatusEnum.COLLECTING) {
                handler.handleDeviceStopCollection(new MessageEntity(device.getDeviceCode()));
            } else if (device.getStatusEnum() == CollectionStatusEnum.FINISHED) {

            }
        });


        // 获取当前操作的仪器信息
        device = DeviceUtil.getMedicalDevice(DeviceEnum.MAI_RUI_T8);

        // 仪器数据控件
        tvDataEcgHr = root.findViewById(R.id.device_data_mairui_t8_ecg);
        tvDataResp = root.findViewById(R.id.device_data_mairui_t8_resp);
        tvDataSpo2 = root.findViewById(R.id.device_data_mairui_t8_spo2);
        tvDataSpo2Pr = root.findViewById(R.id.device_data_mairui_t8_pr);
        tvDataArt = root.findViewById(R.id.device_data_mairui_t8_art);
        tvDataNibp = root.findViewById(R.id.device_data_mairui_t8_nibp);
        tvDataTemp = root.findViewById(R.id.device_data_mairui_t8_temp);
        tvDataCvp = root.findViewById(R.id.device_data_mairui_t8_cvp);
        return root;
    }

    /**
     * 更新数据采集状态
     *
     * @param status 新的状态
     */
    @SuppressLint("ResourceAsColor")
    @Override
    public void updateCollectionStatus(CollectionStatusEnum status) {
        // 此时说明已经成功请求到采集场次号
        if (status == CollectionStatusEnum.WAITING_START) {
            tvControlMessage.setVisibility(View.VISIBLE);
            tvControlMessage.setTextColor(this.getContext().getColor(R.color.collection_status_finished));
        } else if (status == CollectionStatusEnum.COLLECTING) {
            tvCollectionStatus.setText(status.getMessage());
            // 如果是正在采集则修改颜色为红色
            tvCollectionStatus.setTextColor(this.getContext().getColor(R.color.collection_status_collecting));
            tvControlMessage.setText("点击完成采集");
            tvControlMessage.setTextColor(this.getContext().getColor(R.color.collection_status_collecting));
        } else if (status == CollectionStatusEnum.FINISHED) {
            tvCollectionStatus.setText(status.getMessage());
            // 如果是完成采集则修改颜色为绿色
            tvCollectionStatus.setTextColor(this.getContext().getColor(R.color.collection_status_finished));
            tvControlMessage.setText("点击放弃采集");
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void updateSuccessfulUploadCounter(Integer successfulUploadCounter) {
        this.getActivity().runOnUiThread(() -> tvSuccessfulUpdateCounter.setText("" + successfulUploadCounter));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void updateReceiveCounterAndDeviceData(Integer receiveCounter, Object dataObject) {
        this.getActivity().runOnUiThread(() -> {
            tvReceiveCounter.setText("" + receiveCounter);
            // 转换为数据实体
            DataMaiRuiT8 dataMaiRuiT8 = (DataMaiRuiT8) dataObject;
            if (dataMaiRuiT8.getEcgHeartRate() != DataCons.INVALID_DATA_INTEGER) {
                tvDataEcgHr.setText(dataMaiRuiT8.getEcgHeartRate() + " bmp");
            }
            if (dataMaiRuiT8.getRespRespirationRate() != DataCons.INVALID_DATA_INTEGER) {
                tvDataResp.setText(dataMaiRuiT8.getRespRespirationRate() + "");
            }
            if (dataMaiRuiT8.getSpo2PulseRate() != DataCons.INVALID_DATA_INTEGER) {
                tvDataSpo2Pr.setText("" + dataMaiRuiT8.getSpo2PulseRate());
            }
            if (dataMaiRuiT8.getArtIbpMean() != DataCons.INVALID_DATA_DOUBLE) {
                tvDataArt.setText(
                        dataMaiRuiT8.getArtIbpSystolic() + " mmHg\n"
                                + dataMaiRuiT8.getArtIbpDiastolic() + " mmHg\n("
                                + dataMaiRuiT8.getArtIbpMean() + ") mmHg");
            }

            if (dataMaiRuiT8.getNibpDiastolic() != DataCons.INVALID_DATA_DOUBLE) {
                tvDataNibp.setText(dataMaiRuiT8.getNibpSystolic() + " mmHg\n" +
                        dataMaiRuiT8.getNibpDiastolic() + " mmHg\n("
                        + dataMaiRuiT8.getNibpMean() + ") mmHg");
            }
            if (dataMaiRuiT8.getTempTemperature1() != DataCons.INVALID_DATA_DOUBLE) {
                tvDataTemp.setText(dataMaiRuiT8.getTempTemperature1() + " ℃\n" +
                        dataMaiRuiT8.getTempTemperature2() + " ℃\n" +
                        dataMaiRuiT8.getTempTemperatureDifference() + " ℃");
            }
            if (dataMaiRuiT8.getSpo2PercentOxygenSaturation() != DataCons.INVALID_DATA_INTEGER) {
                tvDataSpo2.setText(dataMaiRuiT8.getSpo2PercentOxygenSaturation() + " %");
            }

            if (dataMaiRuiT8.getSpo2PulseRate() != DataCons.INVALID_DATA_INTEGER) {
                tvDataSpo2Pr.setText(dataMaiRuiT8.getSpo2PulseRate() + " bpm");
            }

        });

    }


    /**
     * 将CollectionActivity以Handler的方式传入,可以实现回调
     *
     * @param context 上下文
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // 获取实现接口的activity,将其转换为一个Handler
        handler = (FragmentOperationHandler) getActivity();
    }

}
