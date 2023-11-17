package com.huolala.mockgps.manager

import android.content.Context
import com.baidu.location.BDAbstractLocationListener
import com.baidu.location.BDLocation
import com.baidu.location.LocationClient
import com.baidu.location.LocationClientOption
import com.baidu.mapapi.map.BaiduMap
import com.baidu.mapapi.map.MapStatusUpdateFactory
import com.baidu.mapapi.map.MyLocationData
import com.baidu.mapapi.model.LatLng

/**
 * 定位小蓝点显示控制类
 * @author jiayu.liu
 */
class MapLocationManager(context: Context, private var baiduMap: BaiduMap, follow: Boolean) {
    private var mLocationClient: LocationClient
    private val myLocationListener = object : BDAbstractLocationListener() {
        override fun onReceiveLocation(location: BDLocation?) {
            //mapView 销毁后不在处理新接收的位置
            if (location == null) {
                return
            }
            baiduMap.locationData?.run {
                //如果相等 不能更新
                if (latitude == location.latitude && longitude == location.longitude) {
                    return
                }
            }
            val locData = MyLocationData.Builder()
                .accuracy(location.radius)
                .speed(location.speed)
                .direction(location.direction)
                .latitude(location.latitude)
                .longitude(location.longitude)
                .build()
            baiduMap.setMyLocationData(locData)
            //更新中心点
            if (follow) {
                baiduMap.animateMapStatus(
                    MapStatusUpdateFactory.newLatLngZoom(
                        LatLng(
                            locData.latitude,
                            locData.longitude
                        ), 16f
                    )
                )
            }
        }
    }

    init {
        baiduMap.isMyLocationEnabled = true
        mLocationClient = LocationClient(context)

        //通过LocationClientOption设置LocationClient相关参数
        val option = LocationClientOption()
        option.isOpenGps = true // 打开gps
        option.setScanSpan(1000)

        //设置locationClientOption
        mLocationClient.locOption = option

        mLocationClient.registerLocationListener(myLocationListener)
        //开启地图定位图层
        mLocationClient.start()
    }

    fun onDestroy() {
        mLocationClient.unRegisterLocationListener(myLocationListener)
        mLocationClient.stop()
        baiduMap.isMyLocationEnabled = false
    }

}