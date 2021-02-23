package pro.devapp.currencyrates.ui.rates

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.reactivex.disposables.CompositeDisposable
import pro.devapp.currencyrates.databinding.FragmentRatesBinding
import pro.devapp.currencyrates.ui.common.viewBinding
import pro.devapp.currencyrates.usecases.CreateCurrencyByCodeUseCase
import pro.devapp.currencyrates.usecases.GetRatesListUseCase
import pro.devapp.currencyrates.usecases.LoadRatesListUseCase
import pro.devapp.storage.Storage.getCurrencyDetailsRepository
import pro.devapp.storage.Storage.getCurrencyRatesRepository

class RatesFragment : Fragment() {

    companion object {
        const val TAG = "RatesFragment"
        fun newInstance() = RatesFragment()
    }

    private val viewModel by viewModels<RatesViewModel>() {
        object : ViewModelProvider.NewInstanceFactory() {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return RatesViewModel(
                    requireActivity().application,
                    LoadRatesListUseCase(getCurrencyRatesRepository(requireActivity().applicationContext)),
                    GetRatesListUseCase(getCurrencyRatesRepository(requireActivity().applicationContext)),
                    CreateCurrencyByCodeUseCase(getCurrencyDetailsRepository(requireActivity().applicationContext))
                ) as T
            }
        }
    }

    private val screenBinding by viewBinding(FragmentRatesBinding::inflate)
    private val compositeDisposable = CompositeDisposable()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        screenBinding.currencyList.clickSubject.subscribe {
            viewModel.setSelectedCurrency(it)
        }.also {
            compositeDisposable.add(it)
        }

        screenBinding.currencyList.setTextListener {
            viewModel.setValue(it)
        }

        viewModel.currencyList.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                screenBinding.currencyList.visibility = View.VISIBLE
                screenBinding.progress.visibility = View.GONE
            }
            screenBinding.currencyList.submitList(it)
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            message?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }

        return screenBinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        compositeDisposable.clear()
    }

    override fun onStart() {
        super.onStart()
        viewModel.startRefreshList()
    }

    override fun onStop() {
        super.onStop()
        viewModel.stopRefreshList()
    }
}