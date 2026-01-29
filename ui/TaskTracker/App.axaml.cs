using Avalonia;
using Avalonia.Controls.ApplicationLifetimes;
using Avalonia.Data.Core.Plugins;
using System.Linq;
using Avalonia.Markup.Xaml;
using TaskTracker.ViewModels;
using TaskTracker.Views;
using Microsoft.Extensions.Hosting;
using Microsoft.Extensions.DependencyInjection;
using TaskTracker.Services;
using System;
using TaskTracker.Repositories;
using TaskTracker.Database;
using Microsoft.EntityFrameworkCore;

namespace TaskTracker;

public partial class App : Application
{
    private static IHost _host = RegisterServices();

    public static IServiceProvider Services = _host.Services;

    public override void Initialize()
    {
        AvaloniaXamlLoader.Load(this);
    }

    public override void OnFrameworkInitializationCompleted()
    {
        ConfigureRoutes(Services.GetRequiredService<IRouter>());
        var vm = new MainViewModel(Services.GetRequiredService<IRouter>());

        if (ApplicationLifetime is IClassicDesktopStyleApplicationLifetime desktop)
        {
            // Avoid duplicate validations from both Avalonia and the CommunityToolkit. 
            // More info: https://docs.avaloniaui.net/docs/guides/development-guides/data-validation#manage-validationplugins
            DisableAvaloniaDataAnnotationValidation();
            desktop.MainWindow = new MainWindow(vm);
        }
        else if (ApplicationLifetime is ISingleViewApplicationLifetime singleViewPlatform)
        {
            singleViewPlatform.MainView = new MainView(Services.GetRequiredService<INavigationService>(), vm)
            {
                DataContext = new MainViewModel(Services.GetRequiredService<IRouter>())
            };
        }

        base.OnFrameworkInitializationCompleted();
    }

    private void DisableAvaloniaDataAnnotationValidation()
    {
        // Get an array of plugins to remove
        var dataValidationPluginsToRemove =
            BindingPlugins.DataValidators.OfType<DataAnnotationsValidationPlugin>().ToArray();

        // remove each entry found
        foreach (var plugin in dataValidationPluginsToRemove)
        {
            BindingPlugins.DataValidators.Remove(plugin);
        }
    }

    private static IHost RegisterServices() =>
        Host.CreateDefaultBuilder()
        .ConfigureServices((context, services) =>
        {
            services.AddSingleton<INavigationService, NavigationService>();
            services.AddSingleton<IRouter, Router>();
#if DEBUG
            services.AddDbContext<AppDbContext>(opt => opt.UseInMemoryDatabase("TaskTracker"));
#else
            services.AddSingleton<IRequestProvider, RequestProvider>();
#endif
            services.AddSingleton<UserRepository>();
            services.AddSingleton<TaskRepository>();
        })
        .Build();

    private static void ConfigureRoutes(IRouter router) =>
        router.ConfigureRoutes(configure =>
        {
            configure.AddRoute("/authorization", typeof(AuthorizationViewModel));
            configure.AddRoute("/tasks", typeof(TaskListViewModel));
        });
}