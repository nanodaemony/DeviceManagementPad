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
import com.nano.activity.collection.MessageEntity;
import com.nano.activity.collection.interfaces.FragmentDataExchanger;
import com.nano.activity.collection.interfaces.FragmentOperationHandler;
import com.nano.datacollection.CollectionStatusEnum;
import com.nano.datacollection.parsedata.entity.DataMaiRuiWatoex65;
import com.nano.datacollection.parsedata.entity.DataNuoHe;
import com.nano.device.DeviceEnum;
import com.nano.device.DeviceUtil;
import com.nano.device.MedicalDevice;

import androidx.annotation.NonNull;

/**
 * Description: 迈瑞WATOEX65数据采集器的Fragment
 *
 * @version: 1.0
 * @author: nano
 * @date: 2020/12/29 13:46
 */
public class FragmentMaiRuiWatoex65 extends android.support.v4.app.Fragment implements FragmentDataExchanger {


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

    /**
     * 开始与停止按钮
     */
    private LinearLayout ivCollectionStart;
    private LinearLayout ivCollectionStop;

    // 这里添加各个仪器自己的数据展示控件
    private TextView tvPpeak;
    private TextView tvPmean;
    private TextView tvMv;
    private TextView tvTve;
    private TextView tvRate;
    private TextView tvFio2;
    private TextView tvIe;
    private TextView tvPeep;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_collection_mairui_watoex65, container, false);
        tvCollectionStatus = root.findViewById(R.id.device_collection_status);
        tvReceiveCounter = root.findViewById(R.id.collection_receive_counter);
        tvSuccessfulUpdateCounter = root.findViewById(R.id.successful_update_counter);


        SwipeLayout swipeLayout = root.findViewById(R.id.device_collection_swiplayout);

        // 控制采集开始按钮
        ivCollectionStart = root.findViewById(R.id.device_collection_start);
        // 控制采集结束
        ivCollectionStop = root.findViewById(R.id.device_collection_stop);

        // 控制采集流程(基体逻辑判断交给Activity进行)
        ivCollectionStart.setOnClickListener(view -> {
            handler.handleDeviceStartCollection(new MessageEntity(device.getDeviceCode()));
            swipeLayout.close(true);
        });
        ivCollectionStop.setOnClickListener(view -> {
            handler.handleDeviceStopCollection(new MessageEntity(device.getDeviceCode()));
            swipeLayout.close(true);
        });

        // 获取当前操作的仪器信息
        device = DeviceUtil.getMedicalDevice(DeviceEnum.MAI_RUI_WATOEX65);

        // 这里是仪器特有的参数控件
        tvPpeak = root.findViewById(R.id.device_data_mairui_watoex65_ppeak);
        tvPmean = root.findViewById(R.id.device_data_mairui_watoex65_pmean);
        tvMv = root.findViewById(R.id.device_data_mairui_watoex65_mv);
        tvTve = root.findViewById(R.id.device_data_mairui_watoex65_tve);
        tvRate = root.findViewById(R.id.device_data_mairui_watoex65_rate);
        tvFio2 = root.findViewById(R.id.device_data_mairui_watoex65_fio2);
        tvIe = root.findViewById(R.id.device_data_mairui_watoex65_ie);
        tvPeep = root.findViewById(R.id.device_data_mairui_watoex65_peep);
        return root;
    }


    /**
     * 更新数据采集状态
     * @param status 新的状态
     */
    @Override
    public void updateCollectionStatus(CollectionStatusEnum status) {
        if (status == CollectionStatusEnum.COLLECTING) {
            tvCollectionStatus.setText(status.getMessage());
            // 如果是正在采集则修改颜色为红色
            tvCollectionStatus.setTextColor(this.getContext().getColor(R.color.collection_status_collecting));
            // 隐藏开始采集图标
            ivCollectionStart.setVisibility(View.GONE);
            // 显示结束图标
            ivCollectionStop.setVisibility(View.VISIBLE);
        } else if (status == CollectionStatusEnum.FINISHED) {
            tvCollectionStatus.setText(status.getMessage());
            // 如果是完成采集则修改颜色为绿色
            tvCollectionStatus.setTextColor(this.getContext().getColor(R.color.collection_status_finished));
            // 隐藏结束采集图标(此时只剩下放弃采集图标)
            ivCollectionStop.setVisibility(View.GONE);
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
            DataMaiRuiWatoex65 dataMaiRuiWatoex65 = (DataMaiRuiWatoex65) dataObject;
            tvPpeak.setText(dataMaiRuiWatoex65.getPPeak() + " cmH2O");
            tvPmean.setText(dataMaiRuiWatoex65.getPMean() + " cmH2O");
            tvMv.setText(dataMaiRuiWatoex65.getMv() + " L/min");
            tvTve.setText(dataMaiRuiWatoex65.getTve() + " mL");
            tvRate.setText(dataMaiRuiWatoex65.getRate() + " bpm");
            tvFio2.setText(dataMaiRuiWatoex65.getFiO2() + " %");
            tvIe.setText(dataMaiRuiWatoex65.getIe() + "");
            tvPeep.setText(dataMaiRuiWatoex65.getPeep() + "");
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
