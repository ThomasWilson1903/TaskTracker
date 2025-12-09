using CommunityToolkit.Mvvm.Input;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using TaskTracker.Models;
using TaskTracker.Repositories;

namespace TaskTracker.ViewModels;

public partial class TaskListViewModel : ViewModelBase
{
    private TaskRepository _repository;
    private User _currentUser;

    public ObservableCollection<TaskViewModel> Tasks { get; } = new ObservableCollection<TaskViewModel>();

    public TaskListViewModel(TaskRepository repository, UserRepository userRepository)
    {
        _repository = repository;

        if (userRepository is null)
            return;

        userRepository.Create(new User()).GetAwaiter().GetResult();
        var currentUser = GetCurrentUser(userRepository);
        if (currentUser is not null)
            _currentUser = currentUser;

        FillTasks();
    }

    private static User? GetCurrentUser(UserRepository userRepository) =>
        userRepository.Users.FirstOrDefault();

    private async void FillTasks()
    {
        if (_currentUser is null)
            return;

        var tasks = await _repository.GetAllByUserId(_currentUser.Id).ConfigureAwait(false);

        if (tasks is null || !tasks.Any())
            return;

        foreach (var task in tasks)
        {
            Tasks.Add(new TaskViewModel()
            {
                Id = task.Id,
                Title = task.Title,
                DueDate = task.DueDate,
                CreatedAt = task.CreatedAt,
                UpdatedAt = task.UpdatedAt,
                CompletedAt = task.CompletedAt,
            });
        }
    }

    [RelayCommand]
    private async System.Threading.Tasks.Task CreateTask()
    {
        Tasks.Add(new TaskViewModel());
    }

    [RelayCommand]
    private async System.Threading.Tasks.Task DeleteTask(TaskViewModel task)
    {
        Tasks.Remove(task);
        var t = await _repository.GetById(task.Id).ConfigureAwait(false);
        //if (t is not null)
            //_repository.Delete(task.Id);
    }
}
