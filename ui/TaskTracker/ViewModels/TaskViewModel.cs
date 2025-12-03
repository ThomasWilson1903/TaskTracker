using CommunityToolkit.Mvvm.ComponentModel;
using System;

namespace TaskTracker.ViewModels;

public partial class TaskViewModel : ViewModelBase
{
    [ObservableProperty]
    private Guid _id;

    [ObservableProperty]
    private string _title;

    [ObservableProperty]
    private string _description;

    [ObservableProperty]
    private string _priority;

    [ObservableProperty]
    private DateTime? _dueDate;

    [ObservableProperty]
    private DateTime? _createdAt;

    [ObservableProperty]
    private DateTime? _updatedAt;

    [ObservableProperty]
    private DateTime? _completedAt;

    public TaskViewModel() { }
}
