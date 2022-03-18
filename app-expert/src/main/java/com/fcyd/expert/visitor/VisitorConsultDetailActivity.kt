package com.fcyd.expert.visitor

import android.graphics.Color
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.widget.TextView
import com.fcyd.expert.bean.BeanVisitorConsult
import com.fcyd.expert.bean.BeanVisitorConsultDetail
import com.fcyd.expert.databinding.ActivityVisitorConsultEditBinding
import com.fcyd.expert.vm.VisitorViewModel
import com.mtjk.annotation.MyClass
import com.mtjk.annotation.MyField
import com.mtjk.base.MyBaseActivity
import com.mtjk.base.obs
import com.mtjk.utils.body
import com.mtjk.utils.click
import com.mtjk.utils.toast
import kotlinx.android.synthetic.main.activity_visitor_consult_edit.*

/*
* tag==来访者详情
* */
@MyClass(mToolbarTitle = "来访者详情")
class VisitorConsultDetailActivity : MyBaseActivity<ActivityVisitorConsultEditBinding, VisitorViewModel>() {

    @MyField
    lateinit var consult: BeanVisitorConsult

    lateinit var consultDetail: BeanVisitorConsultDetail

    override fun initCreate() {
        initView()
        loadData()
    }

    private fun initView() {
        with(mBinding) {
            time.text = consult.consultStartTimeDesc()
            title.text = consult.title
            title.setCompoundDrawables(if(consult.consultTypeDrawable() > 0) resources.getDrawable(consult.consultTypeDrawable()) else null, null, null, null)
            setQuestionText(question_type, "问题类型","")
            setQuestionText(question_desc, "问题描述","")
            setQuestionText(question_effect,"期望效果","")
            setEditTitle(main_title, "主诉问题", "(选填)")
            setEditTitle(method_title, "辅助方法", "(选填)")
            setEditTitle(effect_title, "辅助效果", "(选填)")
            setEditTitle(summarize_title, "总结", "(选填)")
            save.click { clickSave() }
        }
    }

    private fun loadData() {
        if(consult?.visitorCaseId.isNullOrEmpty()) {
            return
        }
        mViewModel.getVisitorConsultDetail(consult.visitorCaseId).obs(this@VisitorConsultDetailActivity) {
            it.y {
                consultDetail = it
                var queTypeString = it.questionTypeDesc()
                if (!queTypeString.isNullOrEmpty()) setQuestionText(
                    question_type,
                    "问题类型",
                    queTypeString
                )
                if (!it.description.isNullOrEmpty()) setQuestionText(
                    question_desc,
                    "问题描述",
                    it.description
                )
                if (!it.wishResult.isNullOrEmpty()) setQuestionText(
                    question_effect,
                    "期望效果",
                    it.wishResult
                )
                if (!it.mainQuestion.isNullOrEmpty()) main_edit.setText(it.mainQuestion)
                if (!it.helperMethod.isNullOrEmpty()) method_edit.setText(it.helperMethod)
                if (!it.helperResult.isNullOrEmpty()) effect_edit.setText(it.helperResult)
                if (!it.summary.isNullOrEmpty()) summarize_edit.setText(it.summary)
            }
        }
    }

    private fun setQuestionText(view: TextView, title: String, msg: String) {
        var desc = if(msg.isNullOrEmpty()) "未填写" else msg
        var spannableString = SpannableString(title + ": " + desc)
        var colorSpan = ForegroundColorSpan(Color.parseColor(if(msg.isNullOrEmpty()) "#1F2326" else "#5cb7ca"))
        var styleSpan = StyleSpan(Typeface.BOLD)
        var titleLength = title.length
        spannableString.setSpan(colorSpan, 0, titleLength, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
        spannableString.setSpan(styleSpan, 0, titleLength, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
        view.text = spannableString
    }

    private fun setEditTitle(view: TextView, title: String, msg: String) {
        var spannableString = SpannableString(title + ": " + msg)
        var colorSpan = ForegroundColorSpan(Color.parseColor("#1F2326"))
        var styleSpan = StyleSpan(Typeface.BOLD)
        var titleLength = title.length
        spannableString.setSpan(colorSpan, 0, titleLength, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
        spannableString.setSpan(styleSpan, 0, titleLength, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
        view.text = spannableString
    }

    private fun clickSave() {
        var body = mViewModel.body(
            "id", consult.visitorCaseId,
            "mainQuestion", main_edit.getText(),
            "helperMethod", method_edit.getText(),
            "helperResult", effect_edit.getText(),
            "summary", summarize_edit.getText()
        )
        mViewModel.saveVisitorConsult(body).obs(this@VisitorConsultDetailActivity) {
            it.y { toast("已保存编辑信息") }
            it.n { toast("保存失败") }
        }
    }

}