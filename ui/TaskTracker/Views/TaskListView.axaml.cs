using Avalonia;
using Avalonia.Controls;
using Avalonia.Markup.Xaml;
using TaskTracker.ViewModels;

namespace TaskTracker.Views;

public partial class TaskListView : UserControl
{
    public TaskListView(TaskListViewModel viewModel)
    {
        DataContext = viewModel;

        InitializeComponent();
    }
}