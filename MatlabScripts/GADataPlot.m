function GADataPlot()
% Create a plot with the GA data

cd('../General');
K = importdata('GA_DATA.txt');
plot(K(:,3), K(:,5), K(:,3), K(:,6))
hold on;
xlabel('statistics gathered')
ylabel('Fitness Value')
title('Fitness evolution','FontSize',12)
legend('best fitness','avg fitness','Location','NorthWest')
handle=gcf;
saveas(handle, ['GA_fitness', 'eps'],'epsc')
hold off;
cd('../Functions');
end

