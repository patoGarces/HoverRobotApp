package com.example.hoverrobot.ui.settingsFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.hoverrobot.Models.comms.PidSettings
import com.example.hoverrobot.R
import com.example.hoverrobot.dataStore
import com.example.hoverrobot.databinding.SettingsFragmentBinding
import com.google.android.material.slider.Slider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch


class SettingsFragment : Fragment() {

    private var lastPidSettings : PidSettings? = null

    private lateinit var _binding : SettingsFragmentBinding
    private val binding get()= _binding

    val settingsFragmentViewModel : SettingsFragmentViewModel by viewModels(ownerProducer = { requireActivity() })

    var kpValue: Float = 0f
    var kiValue: Float = 0f
    var kdValue: Float = 0f
    var centerValue: Float = 0f
    var safetyLimitsValue: Float = 0f

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SettingsFragmentBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListener()
        setupObserver()
        getParametersFromStore()
    }

    private fun setupListener(){

        binding.sbPidP.addOnChangeListener { rangeSlider, progressP, fromUser ->
            kpValue = progressP
            binding.tvValueP.text = getString(R.string.value_slider_format).format((kpValue/100))
        }

        binding.sbPidP.addOnSliderTouchListener(object : Slider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: Slider) { }
            override fun onStopTrackingTouch(slider: Slider) {
                settingsFragmentViewModel.setPidTunningToRobot(PidSettings(kpValue,kiValue,kdValue,centerValue,safetyLimitsValue))
            }
        })

        binding.sbPidI.addOnChangeListener { rangeSlider, progressI, fromUser ->
            kiValue = progressI
            binding.tvValueI.text = getString(R.string.value_slider_format).format((kiValue/100))
        }

        binding.sbPidI.addOnSliderTouchListener(object : Slider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: Slider) { }
            override fun onStopTrackingTouch(slider: Slider) {
                settingsFragmentViewModel.setPidTunningToRobot(PidSettings(kpValue,kiValue,kdValue,centerValue,safetyLimitsValue))
            }
        })

        binding.sbPidD.addOnChangeListener { rangeSlider, progressD, fromUser ->
            kdValue = progressD
            binding.tvValueD.text = getString(R.string.value_slider_format).format((kdValue/100))
        }

        binding.sbPidD.addOnSliderTouchListener(object : Slider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: Slider) { }
            override fun onStopTrackingTouch(slider: Slider) {
                settingsFragmentViewModel.setPidTunningToRobot(PidSettings(kpValue,kiValue,kdValue,centerValue,safetyLimitsValue))
            }
        })

        binding.sbCenterAngle.addOnChangeListener { rangeSlider, progressCenter, fromUser ->
            centerValue = progressCenter
            binding.tvValueCenter.text = centerValue.toString()
        }

        binding.sbCenterAngle.addOnSliderTouchListener(object : Slider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: Slider) { }
            override fun onStopTrackingTouch(slider: Slider) {
                settingsFragmentViewModel.setPidTunningToRobot(PidSettings(kpValue,kiValue,kdValue,centerValue,safetyLimitsValue))
            }
        })

        binding.sbSafetyLimits.addOnChangeListener { rangeSlider, progressSafetyLimits, fromUser ->
            safetyLimitsValue = progressSafetyLimits
            binding.tvValueLimits.text = safetyLimitsValue.toString()
        }

        binding.sbCenterAngle.addOnSliderTouchListener(object : Slider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: Slider) { }
            override fun onStopTrackingTouch(slider: Slider) {
                settingsFragmentViewModel.setPidTunningToRobot(PidSettings(kpValue,kiValue,kdValue,centerValue,safetyLimitsValue))
            }
        })

        binding.btnSavePid.setOnClickListener {
            Toast.makeText(requireContext(),"Guardando parametros",Toast.LENGTH_LONG).show()
            saveParameters( PidSettings(kpValue,kiValue,kdValue,centerValue,safetyLimitsValue) )
        }

        binding.btnGetPid.setOnClickListener {
            Toast.makeText(requireContext(),"Obteniendo parametros",Toast.LENGTH_LONG).show()
            getParametersFromStore()
        }
    }

    private fun setupObserver(){
        settingsFragmentViewModel.pidSettingFromRobot.observe(viewLifecycleOwner){
            it?.let{
                if(it != lastPidSettings)
                {
                    binding.sbPidP.value = it.kp
                    binding.sbPidI.value = it.ki
                    binding.sbPidD.value = it.kd
                    binding.sbCenterAngle.value = it.centerAngle

                    lastPidSettings = settingsFragmentViewModel.pidSettingFromRobot.value
                }
            }
        }
    }

    private fun saveParameters(pidSettings: PidSettings){
        lifecycleScope.launch(Dispatchers.IO){
            context?.dataStore?.edit { settingsKey ->
                settingsKey[floatPreferencesKey(KEY_PID_PARAM_P)] = pidSettings.kp
                settingsKey[floatPreferencesKey(KEY_PID_PARAM_I)] = pidSettings.ki
                settingsKey[floatPreferencesKey(KEY_PID_PARAM_D)] = pidSettings.kd
                settingsKey[floatPreferencesKey(KEY_PID_PARAM_CENTER)] = pidSettings.centerAngle
                settingsKey[floatPreferencesKey(KEY_PID_PARAM_SAFETY_LIM)] = pidSettings.safetyLimits
            }
        }
    }

    private fun getParametersFromStore() {
        lifecycleScope.launch(Dispatchers.IO) {
            val paramP = context?.dataStore?.data
                ?.map { preferences ->
                    preferences[floatPreferencesKey(KEY_PID_PARAM_P)]
                }?.first()
            val paramI = context?.dataStore?.data
                ?.map { preferences ->
                    preferences[floatPreferencesKey(KEY_PID_PARAM_I)]
                }?.first()
            val paramD = context?.dataStore?.data
                ?.map { preferences ->
                    preferences[floatPreferencesKey(KEY_PID_PARAM_D)]
                }?.first()
            val paramCenter = context?.dataStore?.data
                ?.map { preferences ->
                    preferences[floatPreferencesKey(KEY_PID_PARAM_CENTER)]
                }?.first()
            val safetyLimits = context?.dataStore?.data
                ?.map { preferences ->
                    preferences[floatPreferencesKey(KEY_PID_PARAM_SAFETY_LIM)]
                }?.first()


            paramP?.let {
                if( it >= 0 && it <= binding.sbPidP.valueTo) {
                    binding.sbPidP.value = it
                }
                else{
                    binding.sbPidP.value = 0.0F
                }
            }
            paramI?.let {
                if( it >= 0 && it <= binding.sbPidI.valueTo) {
                    binding.sbPidI.value = it
                }
                else{
                    binding.sbPidI.value = 0.0F
                }
            }
            paramD?.let {
                if( it >= 0 && it <= binding.sbPidD.valueTo) {
                    binding.sbPidD.value = it
                }
                else{
                    binding.sbPidD.value = 0.0F
                }
            }
            paramCenter?.let {
                if( it >= binding.sbCenterAngle.valueFrom && it <= binding.sbCenterAngle.valueTo) {
                    binding.sbCenterAngle.value = it
                }
                else{
                    binding.sbCenterAngle.value = binding.sbCenterAngle.valueFrom
                }
            }
            safetyLimits?.let {
                if( it >= binding.sbSafetyLimits.valueFrom && it <= binding.sbSafetyLimits.valueTo) {
                    binding.sbSafetyLimits.value = it
                }
                else{
                    binding.sbSafetyLimits.value = binding.sbSafetyLimits.valueFrom
                }
            }
        }
    }


    companion object{
        private const val KEY_PID_PARAM_P = "KEY_PID_P"
        private const val KEY_PID_PARAM_I = "KEY_PID_I"
        private const val KEY_PID_PARAM_D = "KEY_PID_D"
        private const val KEY_PID_PARAM_CENTER = "KEY_PID_CENTER"
        private const val KEY_PID_PARAM_SAFETY_LIM = "KEY_PID_SAFETY"
    }
}