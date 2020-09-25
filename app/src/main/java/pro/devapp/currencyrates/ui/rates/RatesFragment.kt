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
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import pro.devapp.currencyrates.databinding.FragmentRatesBinding
import pro.devapp.currencyrates.ui.viewBinding
import pro.devapp.currencyrates.usecases.GetCurrencyByCodeUseCase
import pro.devapp.currencyrates.usecases.GetRatesListUseCase
import pro.devapp.storage.getCurrencyDetailsRepository
import pro.devapp.storage.getCurrencyRatesRepository

class RatesFragment : Fragment() {

    companion object {
        fun newInstance() = RatesFragment()
    }

    private val viewModel by viewModels<RatesViewModel>() {
        object : ViewModelProvider.NewInstanceFactory() {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return RatesViewModel(
                    requireActivity().application,
                    GetRatesListUseCase(getCurrencyRatesRepository(requireActivity().applicationContext)),
                    GetCurrencyByCodeUseCase(getCurrencyDetailsRepository(requireActivity().applicationContext))
                ) as T
            }
        }
    }

    private val screenBinding by viewBinding(FragmentRatesBinding::inflate)
    private val compositeDisposable = CompositeDisposable()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        screenBinding.currencyList.clickSubject.subscribe {
            viewModel.setSelectedCurrency(it)
        }.also {
            compositeDisposable.add(it)
        }

        screenBinding.currencyList.setTextListener {
            viewModel.setValue(it)
        }

        viewModel.currencyList
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                screenBinding.currencyList.submitList(it)
            }
            .also {
                compositeDisposable.add(it)
            }

        viewModel.currencyList
            .observeOn(AndroidSchedulers.mainThread())
            .firstElement()
            .subscribe {
                screenBinding.currencyList.visibility = View.VISIBLE
                screenBinding.progress.visibility = View.GONE
            }
            .also {
                compositeDisposable.add(it)
            }

        viewModel.errorMessage
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { message ->
                if (message.isNotEmpty()) {
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                }
            }
            .also {
                compositeDisposable.add(it)
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