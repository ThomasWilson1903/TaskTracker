using CommunityToolkit.Mvvm.ComponentModel;
using CommunityToolkit.Mvvm.Input;
using TaskTracker.Services;

namespace TaskTracker.ViewModels;

public partial class MainViewModel : ViewModelBase
{
    private IRouter _router;

    private bool _isAuthorized;

    [ObservableProperty]
    private string _greeting = "Welcome to Avalonia!";

    public MainViewModel(IRouter router)
    {
        _router = router;
    }

    [RelayCommand]
    private void GoToAuthorization()
    {
        if (!_isAuthorized)
            _router.NavigateTo("/authorization");
    }
}
