package com.mentuojiankang.user.activity

import com.mentuojiankang.user.databinding.ActivityPersonalInfoEditBinding
import com.mtjk.annotation.MyClass
import com.mtjk.base.MyBaseActivity
import com.mtjk.base.obs
import com.mtjk.bean.BeanUser
import com.mtjk.utils.*
import com.mtjk.view.DialogPicker
import com.mtjk.vm.AccountViewModel
import com.zaaach.citypicker.CityPicker
import com.zaaach.citypicker.adapter.OnPickListener
import com.zaaach.citypicker.model.City
import com.zaaach.citypicker.model.HotCity
import gorden.rxbus2.RxBus
import kotlinx.android.synthetic.main.activity_personal_info_edit.*

/**
 * tag==个人资料
 * author:chenliang
 * date:2021/11/3
 */
@MyClass(mToolbarTitle = "个人资料")
class PersonalInfoEditActivity :
    MyBaseActivity<ActivityPersonalInfoEditBinding, AccountViewModel>() {
    var intage = 0
    var stringage = ""
    var array = arrayListOf<String>(
        "互联网/IT/电子/通信",
        "交通/物流/贸易/零售",
        "广告/传媒/文化体育",
        "房地产/建筑",
        "教育培训",
        "金融",
        "汽车",
        "消费品",
        "服务业",
        "制药/医疗",
        "机械/制造",
        "能源/环保/化工",
        "其他"
    )
    var user = getBeanUser()
    override fun initCreate() {
        mBinding.data = user
        mViewModel.getUserInfo().obs(this) {
            it.y {
                user = it
                mBinding.data = user
                user?.save()
            }
        }
    }


    override fun initClick() {
        header.click { selectImage(false) }
        sex.click { initSex() }
        marriage.click { initMarriage() }
        age.click { initAge() }
        useraddress.click { initCity() }
        editoccupation.click { selectorHy() }
    }


    private fun initSex() {
        var items = arrayListOf("男", "女")
        var dialog = DialogPicker()
        dialog.setItems(items)
        dialog.setTitle("选择性别")
        dialog.selected {
            when (it) {
                0 -> user?.gender = 1
                1 -> user?.gender = 2
            }

            mBinding.data = user
        }
        dialog.show(this)
    }


    private fun initMarriage() {
        var items = arrayListOf("已婚", "未婚")
        var dialog = DialogPicker()
        dialog.setItems(items)
        dialog.setTitle("选择婚姻")
        dialog.selected {
            marriage.setValue(items[it])
            when (it) {
                0 -> user?.maritalStatus = 1
                1 -> user?.maritalStatus = 2
            }
            mBinding?.data = user

        }
        dialog.show(this)
    }


    private fun initAge() {
        var items = arrayListOf<String>()
        for (item in 16..60) {
            items.add("${item}岁")
        }
        var dialog = DialogPicker()
        dialog.setItems(items)
        dialog.setTitle("选择年龄")
        dialog.selected {
            stringage = items[it]
            age.setValue(stringage)
            user?.age = stringage.replace("岁", "").toInt()
            mBinding?.data = user
        }
        dialog.show(this)
    }


    override fun onBackPressed() {
        user?.occupation = userindustry.editText.text.toString()
        user?.nickName = nicknameuser.editText.text.toString()
        mViewModel.updateUserAccount(user!!).obs(this) {
            it.y {
                user!!.save()
                send(BusCode.REFRESH_MINE_USER_INFO)
            }
            super.onBackPressed()
        }

    }

    override fun onDestroy() {
        super.onDestroy()

    }

    override fun resultSelectImage(array: ArrayList<String>) {
        if (array.isEmpty())
            return
        user?.avatar = "file://" + array?.get(0) ?: ""

        MyImage.uploadApp(array?.get(0)!!) {
            if (it.finish) {
                user?.avatar = it.path
                mBinding.data = user
            }
        }

    }

    private fun initCity() {
        var hotCities = ArrayList<HotCity>()
        hotCities.add(HotCity("北京", "北京", "101010100"))
        hotCities.add(HotCity("上海", "上海", "101020100"))
        hotCities.add(HotCity("广州", "广东", "101280101"))
        hotCities.add(HotCity("深圳", "广东", "101280601"))
        hotCities.add(HotCity("杭州", "浙江", "101210101"))

        CityPicker.from(this) //activity或者fragment
            .enableAnimation(true)
            .setHotCities(hotCities)    //指定热门城市
            .setOnPickListener(object : OnPickListener {
                override fun onPick(position: Int, data: City?) {
                    user?.address = data!!.name
                    useraddress.setValue(data?.name)
                    user?.location = data?.name
                    mBinding?.data = user

                }

                override fun onLocate() {
                }

                override fun onCancel() {
                }
            }).show();
    }

    private fun selectorHy() {
        var dialog = DialogPicker()
        dialog.setItems(array)
        dialog.setTitle("所在行业")
        dialog.selected {
            editoccupation.setValue(array[it])
            user?.industry = array[it]
            mBinding?.data = user

        }
        dialog.show(this)
    }


}