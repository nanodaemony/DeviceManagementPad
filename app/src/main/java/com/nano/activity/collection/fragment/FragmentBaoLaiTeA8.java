package com.nano.activity.collection.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.nano.R;
import com.nano.activity.collection.MessageEntity;
import com.nano.activity.collection.interfaces.FragmentDataExchanger;
import com.nano.activity.collection.interfaces.FragmentOperationHandler;
import com.nano.datacollection.CollectionStatusEnum;
import com.nano.datacollection.parsedata.DataCons;
import com.nano.datacollection.parsedata.entity.DataBaoLaiTeA8;
import com.nano.device.DeviceEnum;
import com.nano.device.DeviceUtil;
import com.nano.device.MedicalDevice;

import androidx.annotation.NonNull;

/**
 * Description: 宝莱特数据采集器的Fragment
 *
 * @version: 1.0
 * @author: nano
 * @date: 2020/12/29 13:46
 */
public class FragmentBaoLaiTeA8 extends Fragment implements FragmentDataExchanger {

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



    // 这里添加各个仪器自己的数据展示控件
    private TextView tvHr;
    private TextView tvPr;
    private TextView tvSpO2;
    private TextView tvTemp;
    private TextView tvNibp;
    private TextView tvIbp1;
    private TextView tvIbp2;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_collection_baolaite_a8, container, false);
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
        device = DeviceUtil.getMedicalDevice(DeviceEnum.BAO_LAI_TE_A8);
        // 仪器参数控件
        tvHr = root.findViewById(R.id.device_data_baolaite_hr);
        tvPr = root.findViewById(R.id.device_data_baolaite_pr);
        tvSpO2 = root.findViewById(R.id.device_data_baolaite_spo2);
        tvTemp = root.findViewById(R.id.device_data_baolaite_temp);
        tvNibp = root.findViewById(R.id.device_data_baolaite_nibp);
        tvIbp1 = root.findViewById(R.id.device_data_baolaite_ibp1);
        tvIbp2 = root.findViewById(R.id.device_data_baolaite_ibp2);
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
            DataBaoLaiTeA8 dataBaoLaiTeA8 = (DataBaoLaiTeA8) dataObject;
            if (dataBaoLaiTeA8.getHr() != DataCons.INVALID_DATA_INTEGER) {
                tvHr.setText(dataBaoLaiTeA8.getHr() + " bpm");
            }
            if (dataBaoLaiTeA8.getPr() != DataCons.INVALID_DATA_INTEGER) {
                tvPr.setText(dataBaoLaiTeA8.getPr() + " bpm");
            }
            if (dataBaoLaiTeA8.getSpo2() != DataCons.INVALID_DATA_INTEGER) {
                tvSpO2.setText(dataBaoLaiTeA8.getSpo2() + "%");
            }
            if (dataBaoLaiTeA8.getTemperature1() != DataCons.INVALID_DATA_DOUBLE) {
                String temp = dataBaoLaiTeA8.getTemperature1() + " ℃\n" + dataBaoLaiTeA8.getTemperature2() + " ℃\n" + dataBaoLaiTeA8.getTemperatureDifference() + " ℃";
                tvTemp.setText(temp);
            }
            if (dataBaoLaiTeA8.getNibpMap() != DataCons.INVALID_DATA_DOUBLE) {
                String nibp = dataBaoLaiTeA8.getNibpSys() + " mmHg\n" + dataBaoLaiTeA8.getNibpDia() + " mmHg\n(" + dataBaoLaiTeA8.getNibpMap() + ") mmHg";
                tvNibp.setText(nibp);
            }
            if (dataBaoLaiTeA8.getIbpDia1() != DataCons.INVALID_DATA_DOUBLE) {
                String ibp1 = dataBaoLaiTeA8.getIbpSys1() + " mmHg\n" + dataBaoLaiTeA8.getIbpDia1() + " mmHg\n(" + dataBaoLaiTeA8.getIbpMap1() + ") mmHg";
                tvIbp1.setText(ibp1);
            }
            if (dataBaoLaiTeA8.getIbpDia2() != DataCons.INVALID_DATA_DOUBLE) {
                String ibp2 = dataBaoLaiTeA8.getIbpSys2() + " mmHg\n" + dataBaoLaiTeA8.getIbpDia2() + " mmHg\n(" + dataBaoLaiTeA8.getIbpMap2() + ") mmHg";
                tvIbp2.setText(ibp2);
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
