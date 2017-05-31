function simpleSatisfactionBar()
%Generates a figure with satisfaction in bars


% Initialization
clear ; close all; clc
X = load('satisfaction.txt');
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Max element of each GA Satisfaction row
XX = max(X(X(:,1)==1,3:end)');
[A,ind] = max(XX); % Index of the max element to plot this run
A = grpstats(X, [X(:,1)]);

xAxis = [50:50:600];
xVal = [mean(reshape(A(1,3:end),50,12),1);
     mean(reshape(A(2,3:end),50,12),1);
     mean(reshape(A(3,3:end),50,12),1)];

bar(xAxis, xVal','grouped')
hold on;
xlabel('time steps')
ylabel('satisfaction')
title('Satisfaction Comparative','FontSize',12)
legend('random','GA','close','Location','NorthWest')
handle=gcf;
saveas(handle, ['bar', 'eps'],'epsc')
hold off;
cd('../Functions');
end

