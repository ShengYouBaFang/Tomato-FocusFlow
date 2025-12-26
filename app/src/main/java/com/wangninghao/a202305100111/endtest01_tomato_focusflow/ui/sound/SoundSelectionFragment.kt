package com.wangninghao.a202305100111.endtest01_tomato_focusflow.ui.sound

import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.data.model.WhiteNoise
import com.wangninghao.a202305100111.endtest01_tomato_focusflow.databinding.FragmentSoundSelectionBinding

/**
 * 白噪音选择Fragment
 * 展示可选择的白噪音列表
 */
class SoundSelectionFragment : Fragment() {

    private var _binding: FragmentSoundSelectionBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SoundSelectionViewModel by viewModels()
    private lateinit var adapter: WhiteNoiseAdapter

    private var previewPlayer: MediaPlayer? = null
    private var currentPreviewId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSoundSelectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeViewModel()
        animateViews()
    }

    private fun animateViews() {
        // 标题动画
        binding.tvTitle.alpha = 0f
        binding.tvTitle.translationY = -50f
        binding.tvTitle.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(400)
            .start()
    }

    private fun setupRecyclerView() {
        adapter = WhiteNoiseAdapter(
            onItemClick = { whiteNoise ->
                handleSelection(whiteNoise)
            },
            onPreviewClick = { whiteNoise ->
                handlePreview(whiteNoise)
            }
        )

        binding.rvSounds.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@SoundSelectionFragment.adapter
        }
    }

    private fun observeViewModel() {
        // 观察白噪音列表
        viewModel.whiteNoiseList.observe(viewLifecycleOwner) { noises ->
            adapter.submitList(noises)
        }

        // 观察当前选中项
        viewModel.selectedWhiteNoise.observe(viewLifecycleOwner) { selected ->
            adapter.setSelectedId(selected.id)
        }
    }

    /**
     * 处理选择
     */
    private fun handleSelection(whiteNoise: WhiteNoise) {
        stopPreview()
        viewModel.selectWhiteNoise(whiteNoise)
    }

    /**
     * 处理试听
     */
    private fun handlePreview(whiteNoise: WhiteNoise) {
        if (currentPreviewId == whiteNoise.id) {
            // 如果正在试听同一个，停止试听
            stopPreview()
        } else {
            // 停止之前的试听，播放新的
            stopPreview()
            startPreview(whiteNoise)
        }
    }

    /**
     * 开始试听
     */
    private fun startPreview(whiteNoise: WhiteNoise) {
        try {
            previewPlayer = MediaPlayer.create(requireContext(), whiteNoise.audioRes).apply {
                isLooping = true
                start()
            }
            currentPreviewId = whiteNoise.id
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 停止试听
     */
    private fun stopPreview() {
        previewPlayer?.apply {
            if (isPlaying) {
                stop()
            }
            release()
        }
        previewPlayer = null
        currentPreviewId = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stopPreview()
        _binding = null
    }
}
