using Avalonia.Controls;
using TaskTracker.ViewModels;

namespace TaskTracker.Views;

public partial class AuthorizationView : UserControl
{
    public AuthorizationView(AuthorizationViewModel viewModel)
    {
        DataContext = viewModel;

        InitializeComponent();
    }
}