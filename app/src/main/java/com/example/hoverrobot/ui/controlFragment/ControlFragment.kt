package com.example.hoverrobot.ui.controlFragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.hoverrobot.data.models.comms.AxisControl
import com.example.hoverrobot.databinding.ControlFragmentBinding

class ControlFragment : Fragment() {

    private val controlViewModel : ControlViewModel by viewModels(ownerProducer = { requireActivity() })

    private lateinit var _binding : ControlFragmentBinding
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ControlFragmentBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.joystickThrottle.setFixedCenter(true)
        binding.joystickDirection.setFixedCenter(true)
        setupListener()
        setupObserver()
    }

    private fun setupListener(){

        binding.joystickDirection.setOnMoveListener{ _, _ ->
            val y = 100 - (binding.joystickThrottle.normalizedY * 2)
            val x = (binding.joystickDirection.normalizedX * 2) -100
            controlViewModel.newCoordinatesJoystick(AxisControl(x,y))
        }

        binding.joystickThrottle.setOnMoveListener{ _, _ ->
            val y = 100 - (binding.joystickThrottle.normalizedY * 2)
            val x = (binding.joystickDirection.normalizedX * 2) -100
            controlViewModel.newCoordinatesJoystick(AxisControl(x,y))
        }
    }

    private fun setupObserver(){
        controlViewModel.joyVisible.observe(viewLifecycleOwner){
            it?.let{
                if(it){
                    binding.joystickThrottle.visibility = View.VISIBLE
                    binding.joystickDirection.visibility = View.VISIBLE
                }
                else{
                    binding.joystickThrottle.visibility = View.GONE
                    binding.joystickDirection.visibility = View.GONE
                }
            }
        }
    }
}